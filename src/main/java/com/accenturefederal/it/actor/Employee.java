package com.accenturefederal.it.actor;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Employee extends BaseActor{


    public Employee (long seed) {
        super(seed);
    }

    public void planDay( Date arrival, Date offwork) {
        plan.clear();
        Calendar cal = Calendar.getInstance();
        addToPlan(new Intent(fuzzTime(arrival,Calendar.MINUTE,60), Intent.Type.ARRIVE));
        addToPlan(new Intent(fuzzTime(offwork,Calendar.MINUTE, 60), Intent.Type.LEAVE));
        // Plan a lunch (70% chance)
        if (random.nextInt(100)<70) {
            if (TimeUnit.HOURS.convert(offwork.getTime()-arrival.getTime(),TimeUnit.MILLISECONDS)>6){
                Date departure = new Date(arrival.getTime()+(4*60+fuzz(30))*60l*1000l);
                Date returnTime = new Date(departure.getTime()+(1*60+fuzz(30))*60l*1000l);
                addToPlan(new Intent(departure, Intent.Type.LEAVE));
                addToPlan(new Intent(returnTime, Intent.Type.ARRIVE));
            }
        }
        // Figure out how many breaks we are going to have
        int hours = (int)((plan.get(plan.size()-1).timeplanned.getTime()-plan.get(0).timeplanned.getTime())/(3600l*1000l));
        int numBreaks = random.nextInt(hours);
        while (numBreaks > 0) {
            // Create a break and add its intents
            numBreaks--;
            Intent largestSpan = findLongestPresence();
            Intent departure = findNextIntent(largestSpan, Intent.Type.LEAVE);
            if (largestSpan != null) {
                long breakStart = largestSpan.timeplanned.getTime()
                        +(long)((float)(departure.timeplanned.getTime()-largestSpan.timeplanned.getTime())*random.nextFloat());
                addToPlan(new Intent(new Date(breakStart), Intent.Type.LEAVE));
                long breakEnd = breakStart+random.nextInt(20*60*1000);
                if (breakEnd >departure.timeplanned.getTime()){
                    // Then we will just stay out
                    plan.remove(departure);
                } else {
                    addToPlan(new Intent(new Date(breakEnd), Intent.Type.ARRIVE));
                }
            }
        }
        // Now find time for a meeting (50 % chance)
        if (random.nextInt(100)>50) {
            Intent largestSpan = findLongestPresence();
            Intent departure = findNextIntent(largestSpan, Intent.Type.LEAVE);
            long duration = departure.timeplanned.getTime()-largestSpan.timeplanned.getTime();
            if (duration > 2*60*60*1000) {
                long breakStart = largestSpan.timeplanned.getTime()
                        +(long)((float)(departure.timeplanned.getTime()-largestSpan.timeplanned.getTime())*random.nextFloat());
                addToPlan(new Intent(new Date(breakStart), Intent.Type.LEAVE));
                long breakEnd = breakStart+random.nextInt(120*60*60*1000);
                if (breakEnd >departure.timeplanned.getTime()){
                    // Then we will just stay out
                    plan.remove(departure);
                }
            }
        }
        // There is a 10% chance that we have something to do internally (supply closet, computer/security/conference room)
        if (random.nextInt(100)<10) {
            // If we do things internally, we may do them more than once
            int internalEventCount = random.nextInt(10);
            List<Intent> intervals = findIntents(Intent.Type.ARRIVE);
            while(internalEventCount>0) {
                internalEventCount--;
                int chosenInterval = random.nextInt(intervals.size());
                Intent departure = findNextIntent(plan.get(chosenInterval), Intent.Type.LEAVE);
                long beginTime = plan.get(chosenInterval).timeplanned.getTime();
                long endTime = departure.timeplanned.getTime();
                long timestamp = beginTime+(long)(random.nextFloat()*(float)(endTime-beginTime));
                addToPlan(new Intent(new Date(timestamp), Intent.Type.SUPPPLYCLOSET));
            }
        }
    }

    /**
     * Find the longest time the employee plans to be present
     * @return
     */
    protected Intent findLongestPresence() {
        List<Intent> arrivals = findIntents(Intent.Type.ARRIVE);
        Intent longestSpan = null;
        long span = -1;
        for (Intent eachArrival : arrivals) {
            Intent departure = findNextIntent(eachArrival, Intent.Type.LEAVE);
            if (departure != null) {
                long thisSpan = departure.timeplanned.getTime()-eachArrival.timeplanned.getTime();
                if (thisSpan > span){
                    span = thisSpan;
                    longestSpan = eachArrival;
                }
            }
        }
        return longestSpan;
    }

    public void perform(Intent intent) {

    }

    public void trigger(Date timestamp, TriggerEvent trigger) {

    }
}
