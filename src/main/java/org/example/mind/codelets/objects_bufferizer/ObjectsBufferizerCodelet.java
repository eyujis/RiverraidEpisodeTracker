package org.example.mind.codelets.objects_bufferizer;

import br.unicamp.cst.core.entities.Codelet;
import br.unicamp.cst.core.entities.Memory;
import br.unicamp.cst.core.entities.MemoryObject;
import br.unicamp.cst.representation.idea.Idea;


public class ObjectsBufferizerCodelet extends Codelet {
    Memory detectedObjectsMO;
    Memory objectsBufferMO;
    private final int BUFFER_SIZE =2;
    private Idea objectsBuffer = new Idea("objectsBuffer","",0);

    @Override
    public void accessMemoryObjects() {
        detectedObjectsMO=(MemoryObject)this.getInput("DETECTED_OBJECTS");
        objectsBufferMO=(MemoryObject) this.getOutput("OBJECTS_BUFFER");
    }

    @Override
    public void calculateActivation() {

    }

    @Override
    public void proc() {
        Idea detectedObjects = (Idea) detectedObjectsMO.getI();
        addElement(detectedObjects);

        if(objectsBuffer.getL().size()>=BUFFER_SIZE) {
            objectsBufferMO.setI(objectsBuffer);
        }

//        if(objectsBufferMO.getI() instanceof Idea) {
//            System.out.println(((Idea)objectsBufferMO.getI()).toStringFull());
//        }
    }

    public void addElement(Idea element) {
        if(objectsBuffer.getL().size()>=BUFFER_SIZE) {
            objectsBuffer.getL().remove(0);
        }
        objectsBuffer.getL().add(element);
    }
}
