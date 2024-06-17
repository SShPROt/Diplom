package other;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ButtonEditor extends DefaultCellEditor {
    private JPanel panel = new JPanel();
    private JButton warnButton = new JButton("Предупредить");
    private JButton kickButton = new JButton("Выгнать");
    private String currentUser;
    private Map<String, ClientHandler> clients;
    private DefaultTableModel myModel;

    public ButtonEditor(JCheckBox checkBox, Map <String, ClientHandler> clients, DefaultTableModel myModel) {
        super(checkBox);
        this.clients = clients;
        this.myModel = myModel;
        panel.setLayout(new FlowLayout());
        panel.add(warnButton);
        panel.add(kickButton);

        warnButton.addActionListener(e -> warnUser(currentUser));
        kickButton.addActionListener(e -> kickUser(currentUser));
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        currentUser = (String) table.getValueAt(row, 0);
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "";
    }

    private void warnUser(String username) {
        if (clients.containsKey(username)) {
            clients.get(username).sendMessage("Предупреждение");
        }
    }

    private void kickUser(String username) {
        if (clients.containsKey(username)) {
            clients.get(username).sendMessage("Выгнать");
            clients.get(username).closeConnection();
            clients.remove(username);
            for (int i = 0; i < myModel.getRowCount(); i++) {
                if (username.equals(myModel.getValueAt(i, 0))) {
                    myModel.setValueAt("Не подключён", i, 1);
                }
            }
        }
    }
}
