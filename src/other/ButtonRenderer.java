package other;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class ButtonRenderer extends JPanel implements TableCellRenderer {
    private JButton warnButton = new JButton("Предупредить");
    private JButton kickButton = new JButton("Выгнать");

    public ButtonRenderer() {
        setLayout(new FlowLayout());
        add(warnButton);
        add(kickButton);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}
