package com.accenturefederal.it.actor;

import java.util.*;

public abstract class BaseActor {

    protected Random random;

    protected Date lastTick;

    public static class Action {
        protected Date timestamp;
        public enum Type {ENTER,LEAVE,SUPPLY}
    }
    public static class Intent {
        protected Type type;
        protected Date timeplanned;
        public enum Type {ARRIVE,LEAVE,SMOKEBREAK,LUNCH,SUPPPLYCLOSET}
        public Intent(Date timeplanned, Type type) {
            this.timeplanned = timeplanned;
            this.type = type;
        }
    }
    public static class TriggerEvent {
        public enum Type {TICK,FIREALARM,FIREALARM_ALLCLEAR}
    }
    public enum State {PRESENT,ABSENT,HUNGRY,BORED,LOCKEDOUT}
    protected List<Action> history = new ArrayList<Action>();
    protected List<Intent> plan = new ArrayList<Intent>();
    protected Collection<State> states = new ArrayList<State>();

    public BaseActor(long seed) {
        random = new Random(seed);

    }

    protected void addToPlan(Intent intent) {
        if (plan.isEmpty()) {plan.add(intent); return;}
        boolean inserted = false;
        for (int index = 0; index < plan.size(); index++) {
            if (plan.get(index).timeplanned.after(intent.timeplanned)){
                plan.add(index, intent);
                inserted = true;
                break;
            }
        }
        if (!inserted) {
            plan.add(intent);
        }
    }


    protected Intent findNextIntent(Intent start, Intent.Type type) {
        boolean after = (start==null);
        for (Intent eachIntent:plan) {
            if (after) {
                if (eachIntent.type==type) {
                    return eachIntent;
                }
            } else {
                if (eachIntent == start) {
                    after = true;
                }
            }
        }
        return null;
    }
    /**
     * Locate all intents of a particular type
     * @return all found intents, empty list if none
     */
    protected List<Intent> findIntents(Intent.Type type) {
        List<Intent> result = new ArrayList<>();
        for ( Intent eachIntent : plan) {
            if (type == eachIntent.type) {
                result.add(eachIntent);
            }
        }
        return result;
    }

    protected Date fuzzTime(Date time, int field, int range) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.add(field,fuzz(range));
        return cal.getTime();
    }

    protected int fuzz(int range) {
        return random.nextInt((2*range))-range;
    }

    public abstract void perform(Intent intent);

    public abstract void trigger(Date timestamp, TriggerEvent trigger);


}
