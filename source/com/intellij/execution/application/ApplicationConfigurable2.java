package com.intellij.execution.application;

import com.intellij.diagnostic.logging.LogConfigurationPanel;
import com.intellij.execution.junit2.configuration.ClassBrowser;
import com.intellij.execution.junit2.configuration.CommonJavaParameters;
import com.intellij.execution.junit2.configuration.ConfigurationModuleSelector;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;

import javax.swing.*;
import java.awt.*;

public class ApplicationConfigurable2 extends SettingsEditor<ApplicationConfiguration>{
  private CommonJavaParameters myCommonJavaParameters;
  private LabeledComponent<TextFieldWithBrowseButton> myMainClass;
  private LabeledComponent<JComboBox> myModule;
  private JPanel myWholePanel;

  private final ConfigurationModuleSelector myModuleSelector;
  private final LogConfigurationPanel myLogConfigurations;
  private JPanel myLogsPanel;

  public ApplicationConfigurable2(final Project project) {
    myModuleSelector = new ConfigurationModuleSelector(project, myModule.getComponent());
    myLogConfigurations = new LogConfigurationPanel(project);
    myLogsPanel.setLayout(new BorderLayout());
    myLogsPanel.add(myLogConfigurations.getLoggerComponent(), BorderLayout.CENTER);
    ClassBrowser.createApplicationClassBrowser(project, myModuleSelector).setField(getMainClassField());
  }

  public void applyEditorTo(final ApplicationConfiguration configuration) throws ConfigurationException {
    myCommonJavaParameters.applyTo(configuration);
    myModuleSelector.applyTo(configuration);
    myLogConfigurations.saveTo(configuration);
    configuration.MAIN_CLASS_NAME = getMainClassField().getText();
  }

  public void resetEditorFrom(final ApplicationConfiguration configuration) {
    myCommonJavaParameters.reset(configuration);
    myModuleSelector.reset(configuration);
    myLogConfigurations.restoreFrom(configuration);
    getMainClassField().setText(configuration.MAIN_CLASS_NAME);
  }

  public TextFieldWithBrowseButton getMainClassField() {
    return myMainClass.getComponent();
  }

  public CommonJavaParameters getCommonJavaParameters() {
    return myCommonJavaParameters;
  }

  public JComponent createEditor() {
    return myWholePanel;
  }

  public void disposeEditor() {
  }

  public String getHelpTopic() {
    return null;
  }
}
