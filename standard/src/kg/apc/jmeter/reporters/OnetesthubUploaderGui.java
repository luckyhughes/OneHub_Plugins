package kg.apc.jmeter.reporters;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;

import javax.swing.*;

import kg.apc.jmeter.JMeterPluginsUtils;
import kg.apc.jmeter.gui.BrowseAction;
import kg.apc.jmeter.gui.GuiBuilderHelper;

import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.gui.AbstractVisualizer;
import com.onetesthub.jmeter.OnetesthubAPIClient;

public class OnetesthubUploaderGui
        extends AbstractVisualizer {

    public static final String WIKIPAGE = "OnetesthubUploader";
    private JTextField testTitle;
    private JTextArea uploadToken;
    private JTextField projectKey;
    private JTextField apiUrl;
    private JTextArea infoArea;
    private JComboBox colorFlag;
    private JCheckBox useOnline;

    public OnetesthubUploaderGui() {
        super();
        init();
        initFields();
    }

    @Override
    protected Component getFilePanel() {
        return new JPanel();
    }

    @Override
    public String getStaticLabel() {
        return JMeterPluginsUtils.prefixLabel("OnetesthubUploader");
    }

    @Override
    public String getLabelResource() {
        return getClass().getCanonicalName();
    }

    @Override
    public TestElement createTestElement() {
        TestElement te = new OnetesthubUploader();
        modifyTestElement(te);
        te.setComment(JMeterPluginsUtils.getWikiLinkText(WIKIPAGE));
        return te;
    }

    @Override
    public void modifyTestElement(TestElement te) {
        super.modifyTestElement(te);
        if (te instanceof OnetesthubUploader) {
        	OnetesthubUploader fw = (OnetesthubUploader) te;
            fw.setProject(projectKey.getText());
            fw.setUploadToken(uploadToken.getText());
            fw.setColorFlag(indexToColor(colorFlag.getSelectedIndex()));
            fw.setTitle(testTitle.getText());
            fw.setTitle(apiUrl.getText());
            fw.setUseOnline(useOnline.isSelected());
        }
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        OnetesthubUploader fw = (OnetesthubUploader) element;
        projectKey.setText(fw.getProject());
        uploadToken.setText(fw.getUploadToken());
        apiUrl.setText(fw.getApiUrl());
        colorFlag.setSelectedIndex(colorToIndex(fw.getColorFlag()));
        testTitle.setText(fw.getTitle());
        useOnline.setSelected(fw.isUseOnline());
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());

        add(JMeterPluginsUtils.addHelpLinkToPanel(makeTitlePanel(), WIKIPAGE), BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridBagLayout());

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;

        GridBagConstraints editConstraints = new GridBagConstraints();
        editConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        editConstraints.weightx = 1.0;
        editConstraints.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Initiate active test: ", JLabel.RIGHT));
        addToPanel(mainPanel, editConstraints, 1, row, useOnline = new JCheckBox());

        row++;
        addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Upload to Project: ", JLabel.RIGHT));
        addToPanel(mainPanel, editConstraints, 1, row, projectKey = new JTextField(20));

        editConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        labelConstraints.insets = new java.awt.Insets(2, 0, 0, 0);

        row++;
        addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Upload Metric Url: ", JLabel.RIGHT));
        addToPanel(mainPanel, editConstraints, 1, row, apiUrl = new JTextField(20));

        row++;
        addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Test Title: ", JLabel.RIGHT));
        addToPanel(mainPanel, editConstraints, 1, row, testTitle = new JTextField(20));

        row++;
        addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Color Flag: ", JLabel.RIGHT));
        addToPanel(mainPanel, editConstraints, 1, row, colorFlag = new JComboBox(OnetesthubAPIClient.colors));

        editConstraints.fill = GridBagConstraints.BOTH;

        editConstraints.insets = new java.awt.Insets(4, 0, 0, 0);
        labelConstraints.insets = new java.awt.Insets(4, 0, 0, 0);

        row++;
        addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Upload Token: ", JLabel.RIGHT));

        uploadToken = new JTextArea();
        uploadToken.setLineWrap(true);
        addToPanel(mainPanel, editConstraints, 1, row, GuiBuilderHelper.getTextAreaScrollPaneContainer(uploadToken, 6));

        row++;
        addToPanel(mainPanel, labelConstraints, 0, row, new JLabel("Info Area: ", JLabel.RIGHT));
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setOpaque(false);

        addToPanel(mainPanel, editConstraints, 1, row, GuiBuilderHelper.getTextAreaScrollPaneContainer(infoArea, 6));

        JPanel container = new JPanel(new BorderLayout());
        container.add(mainPanel, BorderLayout.NORTH);
        add(container, BorderLayout.CENTER);
    }

    private void initFields() {
        testTitle.setText("");
        projectKey.setText("DEFAULT");
        uploadToken.setText("Replace this text with upload token received at Onetsthub\nRemember that anyone who has this token can upload files to your account.\nPlease, treat your token as confidential data.\nSee plugin help for details.");
        apiUrl.setText("");
        colorFlag.setSelectedIndex(0);
        useOnline.setSelected(true);
    }

    private void addToPanel(JPanel panel, GridBagConstraints constraints, int col, int row, JComponent component) {
        constraints.gridx = col;
        constraints.gridy = row;
        panel.add(component, constraints);
    }

    @Override
    public void clearGui() {
        super.clearGui();
        initFields();
    }

    @Override
    public void add(SampleResult sr) {
    }

    @Override
    public void clearData() {
        infoArea.setText("");
    }

    public void inform(String string) {
        infoArea.append(string + "\n");
    }

    @Override
    public boolean isStats() {
        return false;
    }

    private String indexToColor(int selectedIndex) {
        if (selectedIndex >= 0) {
            return OnetesthubAPIClient.colors[selectedIndex];
        } else {
            return OnetesthubAPIClient.COLOR_NONE;
        }
    }

    private int colorToIndex(String colorFlag) {
        return Arrays.asList(OnetesthubAPIClient.colors).indexOf(colorFlag);
    }
}
