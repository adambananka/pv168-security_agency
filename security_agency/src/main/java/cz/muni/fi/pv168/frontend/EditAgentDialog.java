package cz.muni.fi.pv168.frontend;

import cz.muni.fi.pv168.backend.agent.Agent;
import cz.muni.fi.pv168.backend.agent.AgentManager;
import cz.muni.fi.pv168.backend.common.ValidationException;

import javax.swing.*;
import java.awt.event.*;
import java.util.ResourceBundle;

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

        setTitle("Edit Agent Dialog"); //TODO localize
        setLocationRelativeTo(this);
        agentNameField.setText(agent.getName());
        agentRankSlider.setValue(agent.getRank());
        if (agent.isAlive()) {
            trueRadioButton.setSelected(true);
        } else {
            falseRadioButton.setSelected(true);
        }
    }

    private void onOK() {
        agent.setName(agentNameField.getText());
        agent.setRank(agentRankSlider.getValue());
        if (trueRadioButton.isSelected()) {
            agent.setAlive(true);
        } else {
            agent.setAlive(false);
        }

        try {
            agentManager.updateAgent(agent);
            dispose();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage() + " Please, correct it."); //TODO localize
            dispose();
            EditAgentDialog dialog = new EditAgentDialog(agentManager, agent, bundle);
            dialog.pack();
            dialog.setVisible(true);
        }
        //TODO check
    }

    private void onCancel() {
        // add your code here if necessary
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
}
