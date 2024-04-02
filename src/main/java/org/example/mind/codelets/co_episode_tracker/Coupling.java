package org.example.mind.codelets.co_episode_tracker;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.object_proposer.ObjectComparator;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Coupling {
    // spatial relations
    public static boolean haveCouplingConditions(Idea eventX, Idea eventY, String relationType, Idea rcvSOEpisodes) {
        if(relationType == null) {
            return false;
        }


        // if the object is the same, it attends a spatial relation with itself
        if(sameObjectId(eventX, eventY)) {
            return true;
        }

        Idea event1, event2;

        if(isInverseRelation(relationType)) {
            event2 = eventX.clone();
            event1 = eventY.clone();
        } else {
            event1 = eventX.clone();
            event2 = eventY.clone();
        }

        Idea previousObjectVectorEvent2 = previousObjectVectorEvent(event2, rcvSOEpisodes);

        boolean isObject2FromPreviousEvent = false;
        if(previousObjectVectorEvent2!=null) {
            isObject2FromPreviousEvent = true;
            event2 = previousObjectVectorEvent2;
        }

        Idea object1 = getObject1(event1);
        Idea object2 = getObject2(event2, isObject2FromPreviousEvent);

        if(new ObjectComparator().areBoundingRectAdjacent(object1, object2)){
            return true;
        }

        BoundRect boundRect1 = new BoundRect(object1);
        BoundRect boundRect2 = new BoundRect(object2);

        double[] speedVector1 = getCategoryVector(event1);
        double[] speedVector2 = getCategoryVector(event2);

        //relative movement
        double dx = speedVector1[0]-speedVector2[0];
        double dy = speedVector1[1]-speedVector2[1];

        return detectSweptAABBCollision(boundRect1, boundRect2, dx, dy);
    }

    private static boolean detectSweptAABBCollision(BoundRect boundRect1, BoundRect boundRect2, double dx, double dy) {
        double xInvEntry, yInvEntry;
        double xInvExit, yInvExit;

        if(dx>0) {
            xInvEntry = boundRect2.tl.x - boundRect1.br.x;
            xInvExit = boundRect2.br.x - boundRect1.tl.x;
        } else {
            xInvEntry = boundRect2.br.x - boundRect1.tl.x;
            xInvExit = boundRect2.tl.x - boundRect1.br.x;
        }

        if(dy>0) {
            yInvEntry = boundRect2.tl.y - boundRect1.br.y;
            yInvExit = boundRect2.br.y - boundRect1.tl.y;
        } else {
            yInvEntry = boundRect2.br.y - boundRect1.tl.y;
            yInvExit = boundRect2.tl.y - boundRect1.br.y;
        }

        double xEntry, yEntry;
        double xExit, yExit;

        if(dx==0) {
            xEntry = Double.NEGATIVE_INFINITY;
            xExit = Double.POSITIVE_INFINITY;
        } else {
            xEntry = xInvEntry/dx;
            xExit = xInvExit/dx;
        }

        if(dy==0) {
            yEntry = Double.NEGATIVE_INFINITY;
            yExit = Double.POSITIVE_INFINITY;
        } else {
            yEntry = yInvEntry/dy;
            yExit = yInvExit/dy;
        }

        double entryTime = Math.max(xEntry, yEntry);
        double exitTime = Math.min(xExit, yExit);

        if(entryTime > exitTime || xEntry < 0 && yEntry < 0 || xEntry > 1 || yEntry > 1) {
            return false;
        }

        return true;
    }

    private static double[] getCategoryVector(Idea event) {
        if(isVectorCategory(event)) {
            double[] categoryVector = (double[]) event.get("categoryVector").getValue();
            return categoryVector;
        }
        return new double[] {0.0, 0.0};
    }

    private static Idea getObject2(Idea event, boolean fromPreviousEvent) {
        if(isVectorCategory(event) && !fromPreviousEvent) {
            return event.get("initialObjectState");
        } else {
            return event.get("lastObjectState");
        }
    }

    private static Idea getObject1(Idea event) {
        return event.get("lastObjectState");
    }

    private static boolean isVectorCategory(Idea event) {
        String categoryName = (String) event.get("eventCategory").getValue();
        if(categoryName.startsWith("Vector")) {
            return true;
        }
        return false;
    }

    private static boolean isInverseRelation(String relationType) {
        if(relationType.endsWith("i")) {
            return true;
        }
        return false;
    }

    private static boolean sameObjectId(Idea ex, Idea ey) {
        int objectIdx = (int) ex.get("objectId").getValue();
        int objectIdy = (int) ey.get("objectId").getValue();

        return objectIdx==objectIdy;
    }

    private static Idea previousObjectVectorEvent(Idea event2, Idea rcvSOEpisodes) {
        int objectId = (int) event2.get("objectId").getValue();
        int eventId = (int) event2.get("eventId").getValue();
        int currentTimestamp = (int) event2.get("currentTimestamp").getValue();
        int previousTimestamp = currentTimestamp - 1;

        List<Idea> previousObjectVectorEvents= rcvSOEpisodes.getL().stream()
                .filter(episode -> (int) episode.get("objectId").getValue() == objectId
                && (int) episode.get("eventId").getValue() != eventId
                && (int) episode.get("currentTimestamp").getValue() == previousTimestamp
                && isVectorCategory(episode)).collect(Collectors.toList());

        if(previousObjectVectorEvents.size()>1) {
            Logger.getLogger(Coupling.class.getName()).log(Level.SEVERE,
                    "The object from the event2 has more than one previous event.");
        } else if(previousObjectVectorEvents.size()==1) {
            return previousObjectVectorEvents.get(0);
        }

        return null;
    }
}
