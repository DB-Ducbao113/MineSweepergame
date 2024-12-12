import javax.swing.*;
import java.awt.*;

public class MineTile extends JButton {
    int r, c;

    public MineTile(int r, int c) {
        this.r = r;
        this.c = c;
        setFocusable(false);
        setMargin(new Insets(0, 0, 0, 0));
        setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
    }
}
