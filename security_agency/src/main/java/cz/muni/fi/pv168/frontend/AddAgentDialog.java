package cz.muni.fi.pv168.frontend;

import cz.muni.fi.pv168.backend.agent.Agent;
import cz.muni.fi.pv168.backend.agent.AgentManager;
import cz.muni.fi.pv168.backend.common.ValidationException;

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
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        setTitle(bundle.getString("AddAgentDialog")); //TODO localize
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
        /*try {
            agentManager.createAgent(agent);
            dispose();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(null, bundle.getString(ex.getMessage()) + bundle.getString("Please, correct" +
                    " it."), bundle.getString("Message"), 0); //TODO localize
            dispose();
            new AddAgentDialog(agentManager, bundle);
        }*/
        //TODO check
    }

    private void onCancel() {
        // add your code here if necessary
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
                            " it."), bundle.getString("Message"), 0); //TODO localize
                    new AddAgentDialog(agentManager, bundle);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
