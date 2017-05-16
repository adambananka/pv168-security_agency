package cz.muni.fi.pv168.frontend;

import cz.muni.fi.pv168.backend.agent.Agent;
import cz.muni.fi.pv168.backend.agent.AgentManager;
import cz.muni.fi.pv168.backend.common.ValidationException;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * @author Adam Baňanka, Daniel Homola
 */
public class AddAgentDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField agentNameField;
    private JSlider agentRankSlider;

    private AgentManager agentManager;
    private ResourceBundle bundle;

    public AddAgentDialog(AgentManager manager, ResourceBundle bundle) {
        agentManager = manager;
        this.bundle = bundle;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setTitle(bundle.getString("AddAgentDialog"));
        setLocationRelativeTo(this);

        pack();
        setVisible(true);
    }

    private void onOK() {
        Agent agent = new Agent();
        agent.setName(agentNameField.getText());
        agent.setAlive(true);
        agent.setRank(agentRankSlider.getValue());

        new AddAgentWorker(agent).execute();
    }

    private void onCancel() {
        dispose();
    }

    public class AddAgentWorker extends SwingWorker<Exception, Void> {
        private Agent agent;

        public AddAgentWorker(Agent agent) {
            this.agent = agent;
        }

        @Override
        protected Exception doInBackground() throws Exception {
            try {
                agentManager.createAgent(agent);
            } catch (ValidationException ex) {
                return ex;
            }
            return null;
        }

        @Override
        protected void done() {
            dispose();
            try {
                Exception ex = get();
                if (ex != null) {
                    JOptionPane.showMessageDialog(null, bundle.getString(ex.getMessage()) + bundle.getString("Please, correct" +
                            " it."), bundle.getString("Message"), JOptionPane.ERROR_MESSAGE);
                    new AddAgentDialog(agentManager, bundle);
                }
            } catch (InterruptedException | ExecutionException e) {
                LoggerFactory.getLogger(AddAgentDialog.class).error("Worker get() error.", e);
            }
        }
    }
}
