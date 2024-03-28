package org.example.mind.codelets.co_episode_tracker;
import br.unicamp.cst.representation.idea.Idea;
import org.opencv.core.Point;

public class BoundRect {
    public Point tl;
    public Point br;

    public BoundRect(Idea object) {
        this.tl = new Point((double) object.get("boundRect.tl.x").getValue(),
                (double) object.get("boundRect.tl.y").getValue());
        this.br = new Point((double) object.get("boundRect.br.x").getValue(),
                (double) object.get("boundRect.br.y").getValue());
    }
}
