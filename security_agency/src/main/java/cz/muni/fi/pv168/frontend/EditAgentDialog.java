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
public class EditAgentDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField agentNameField;
    private JRadioButton trueRadioButton;
    private JRadioButton falseRadioButton;
    private JSlider agentRankSlider;

    private AgentManager agentManager;
    private Agent agent;
    private ResourceBundle bundle;

    public EditAgentDialog(AgentManager manager, Agent agent, ResourceBundle bundle) {
        agentManager = manager;
        this.agent = agent;
        this.bundle = bundle;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        trueRadioButton.addActionListener(e -> onTrueRadioButton());
        falseRadioButton.addActionListener(e -> onFalseRadioButton());
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

        setTitle(bundle.getString("EditAgentDialog"));
        setLocationRelativeTo(this);
        agentNameField.setText(agent.getName());
        agentRankSlider.setValue(agent.getRank());
        if (agent.isAlive()) {
            trueRadioButton.setSelected(true);
        } else {
            falseRadioButton.setSelected(true);
        }

        pack();
        setVisible(true);
    }

    private void onOK() {
        agent.setName(agentNameField.getText());
        agent.setRank(agentRankSlider.getValue());
        if (trueRadioButton.isSelected()) {
            agent.setAlive(true);
        } else {
            agent.setAlive(false);
        }

        new EditAgentWorker().execute();
    }

    private void onCancel() {
        dispose();
    }

    private void onTrueRadioButton() {
        trueRadioButton.setSelected(true);
        falseRadioButton.setSelected(false);
    }

    private void onFalseRadioButton() {
        trueRadioButton.setSelected(false);
        falseRadioButton.setSelected(true);
    }

    public class EditAgentWorker extends SwingWorker<Exception, Void> {

        @Override
        protected Exception doInBackground() throws Exception {
            try {
                agentManager.updateAgent(agent);
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
                if (ex != null ) {
                    JOptionPane.showMessageDialog(null, bundle.getString(ex.getMessage()) + bundle.getString("Please, correct" +
                            " it."), bundle.getString("Message"), JOptionPane.ERROR_MESSAGE);
                    new EditAgentDialog(agentManager, agent, bundle);
                }
            } catch (InterruptedException | ExecutionException e) {
                LoggerFactory.getLogger(EditMissionDialog.class).error("Worker get() error.", e);
            }

        }
    }
}
