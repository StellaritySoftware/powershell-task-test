package pages

import commonpages.CreateNewPlanConfigureTasksPage
import geb.Page
import geb.navigator.Navigator
import org.openqa.selenium.WebElement

/**
 * Created by Kateryna on 05.12.2017.
 */
class PowerShellTaskConfigurationPage extends Page {

    static url = {"/bamboo/build/admin/create/createPlanTasks.action"}
    static at = { ($("#createTask h2").text() == "PowerShell Task configuration" ||
                $("#updateTask h2").text() == "PowerShell Task configuration")
    }

    static content = {
        taskDescription { $("#createTask_userDescription")}
        taskDescriptionUpdate {$("#updateTask_userDescription")}
        disabletaskCheckbox {$("#createTask_taskDisabled")}
        disabletaskCheckboxUpdate {$("#updateTask_taskDisabled")}
        scriptLocation {$('select', name:'psLocation')}
        scriptFile {$("#psFile")}
        arguments {$("#psArguments")}
        environmentVariable {$("#psEnvironment")}
        subdirectory {$("#psSubdirectory")}
        bitness {$("#psBitness")}
        loadUserProfile {$("#psLoadProfile")}
        timeout{$("#boosttestTimeout")}
        successfulTaskCreationText {$("div.aui-message.aui-message-success").text() == "Task created successfully."}
        successfulTaskUpdatedText {$("div.aui-message.aui-message-success").text() == "Task saved successfully."}
        collapseSection{$("div.collapsible-details")}
        inlineScriptBody{$("#psBody textarea")}

    }

    def clickSave(){
        js.exec(
            "var createSave = document.getElementById('createTask_save');" +
            "var updateSave = document.getElementById('updateTask_save');" +
            "createSave ? createSave.click() : updateSave.click();"
        )
        browser.waitFor{successfulTaskCreationText || successfulTaskUpdatedText}
        browser.at CreateNewPlanConfigureTasksPage
    }

    def uncollapseAdvancedOptions(){
        js."document.querySelector('fieldset.collapsible-section.collapsed div.summary span.icon.icon-expand').scrollIntoView()"
        js."document.querySelector('fieldset.collapsible-section.collapsed div.summary span.icon.icon-expand').click()"
        waitFor{collapseSection.isDisplayed()}
    }

    def setEnvironmenVariables(String variables) {
        js."document.querySelector('#psEnvironment').scrollIntoView()"
        environmentVariable = variables
    }

    def getSelectedText(Navigator select) {
        return select.find('option', value:select.value()).text()
    }

    def getTextOfInlineScriptBody() {
        ArrayList<WebElement> list = browser.find("div#psBody div.ace_layer.ace_text-layer div.ace_line").allElements().toList()
        StringBuilder builder = new StringBuilder()

        for(item in list){
            builder.append(item.getText() + "\n")
        }

        return builder.toString().trim()
    }
 }
