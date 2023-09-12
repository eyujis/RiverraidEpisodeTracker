package org.example.mind.codelets.event_cat_learner.entities;

import br.unicamp.cst.representation.idea.Idea;
import org.example.mind.codelets.event_cat_learner.EventCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectsTransitionsExtractor {

    public Idea extract(Idea objectsBuffer) {

        Idea objectsTransitions = new Idea("objectsTransitions", "", 0);
        List<Integer> interIds = objectIdsIntersectingBetweenTimesSteps(objectsBuffer);

        for (int id: interIds) {
            Idea objectTransition = new Idea("objectTransition", "", 0);

            objectTransition.add(new Idea("objectId", id));

            Idea timeSteps = new Idea("timeSteps", "", 0);

            for (int i=0; i<objectsBuffer.getL().size(); i++) {

                for (Idea object : objectsBuffer.getL().get(i).get("objects").getL()) {
                    int objId = (int) object.get("id").getValue();

                    if(id==objId) {
                        Idea timeStep = new Idea("timeStep", "", 0);
                        for(Idea propertyState : object.getL()) {
                            if(propertyState.getName()!="id" && propertyState.getName()!="colorId") {
                                timeStep.add(propertyState);
                            }
                        }
                        timeStep.add(objectsBuffer.getL().get(i).get("timestamp"));
                        timeSteps.add(timeStep);
                    }
                }
            }
            objectTransition.add(timeSteps);
            objectsTransitions.add(objectTransition);
        }
        return objectsTransitions;
    }

    // TODO this restricts the implementation only for objects present in all frames.
    // It will not cover the events where objects appear and disappear (e.g., explosions).
    public List<Integer> objectIdsIntersectingBetweenTimesSteps(Idea objectsBuffer) {
        List<Integer> resultIds = extractIdsFromObjects(objectsBuffer.getL().get(0).get("objects"));

        for (int i=1; i<objectsBuffer.getL().size(); i++) {
            List<Integer> currentIds = extractIdsFromObjects(objectsBuffer.getL().get(i).get("objects"));

            // source: https://www.baeldung.com/java-lists-intersection
            resultIds = resultIds.stream()
                    .distinct()
                    .filter(currentIds::contains)
                    .collect(Collectors.toList());
        }

        return resultIds;
    }


    public List<Integer> extractIdsFromObjects(Idea objects) {
        List<Integer> detectedObjectsIds = new ArrayList<>();

        for (int j=0; j<objects.getL().size(); j++) {
            detectedObjectsIds.add((Integer) objects.getL().get(j).get("id").getValue());
        }
        return detectedObjectsIds;
    }

}
