import commonpages.LoginPage
import configuration.CommonConfig
import geb.spock.GebReportingSpec
import pages.PowerShellTaskConfigurationPage
import pages.TaskTypesPage

class EditFieldsTest extends GebReportingSpec
{

    def run()
    {
        String script =  "dir /home\n" +
                "echo \$args[0]\n" +
                "Get-Variable | Out-String\n" +
                "Get-ChildItem Env:\n" +
                "exit 10"

        when:

        def loginPage = browser.to LoginPage

        def dashboardPage = loginPage.login(CommonConfig.user, CommonConfig.password)

        def createNewPlanConfigurePlanPage = dashboardPage.createNewPlan()
        createNewPlanConfigurePlanPage.setRandomProjectPlanNames()
        createNewPlanConfigurePlanPage.setNoneRepository()

        def configureTasksPage = createNewPlanConfigurePlanPage.clickConfigurePlanButton()

        def tasks = configureTasksPage.addTask(TaskTypesPage)

        PowerShellTaskConfigurationPage powerShellTaskConfiguration = tasks.selectPowerShellTesttask()
        powerShellTaskConfiguration.taskDescription << "my_task"
        powerShellTaskConfiguration.disabletaskCheckbox = true
        powerShellTaskConfiguration.scriptLocation = "Inline"
        powerShellTaskConfiguration.inlineScriptBody = script
        powerShellTaskConfiguration.arguments = "Test"
        powerShellTaskConfiguration.subdirectory << "subDir"
        powerShellTaskConfiguration.setEnvironmenVariables "JAVA_OPTS=-Xmx256m -Xms128m"
        powerShellTaskConfiguration.uncollapseAdvancedOptions()
        powerShellTaskConfiguration.bitness = "x86"
        powerShellTaskConfiguration.loadUserProfile = "Yes"

        powerShellTaskConfiguration.clickSave()

        configureTasksPage.editTask(PowerShellTaskConfigurationPage)

        then:
        powerShellTaskConfiguration.taskDescriptionUpdate.value() == "my_task"
        powerShellTaskConfiguration.disabletaskCheckboxUpdate == "true"
        powerShellTaskConfiguration.getSelectedText(powerShellTaskConfiguration.scriptLocation) == "Inline"

        powerShellTaskConfiguration.getTextOfInlineScriptBody() == script

        powerShellTaskConfiguration.arguments == "Test"
        powerShellTaskConfiguration.subdirectory == "subDir"

        when:
        powerShellTaskConfiguration.uncollapseAdvancedOptions()

        then:
        powerShellTaskConfiguration.environmentVariable.value() == "JAVA_OPTS=-Xmx256m -Xms128m"
        powerShellTaskConfiguration.getSelectedText(powerShellTaskConfiguration.bitness) == "x86"
        powerShellTaskConfiguration.getSelectedText(powerShellTaskConfiguration.loadUserProfile)  == "Yes"

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        when:
        configureTasksPage.editTask(PowerShellTaskConfigurationPage)

        powerShellTaskConfiguration.taskDescriptionUpdate = ""
        powerShellTaskConfiguration.disabletaskCheckboxUpdate = false
        powerShellTaskConfiguration.scriptLocation = "File"
        waitFor {powerShellTaskConfiguration.scriptFile.isDisplayed()}
        powerShellTaskConfiguration.scriptFile = "TestFile"
        powerShellTaskConfiguration.arguments = ""
        powerShellTaskConfiguration.subdirectory = ""
        powerShellTaskConfiguration.bitness = "Any"
        powerShellTaskConfiguration.loadUserProfile = "Default"
        powerShellTaskConfiguration.setEnvironmenVariables ""

        powerShellTaskConfiguration.clickSave()

        configureTasksPage.editTask(PowerShellTaskConfigurationPage)

        then:
        powerShellTaskConfiguration.taskDescriptionUpdate.value() == ""
        powerShellTaskConfiguration.disabletaskCheckboxUpdate.value() == null
        powerShellTaskConfiguration.getSelectedText(powerShellTaskConfiguration.scriptLocation) == "File"
        powerShellTaskConfiguration.inlineScriptBody.displayed == false
        powerShellTaskConfiguration.arguments == ""
        powerShellTaskConfiguration.subdirectory == ""

        when:
        powerShellTaskConfiguration.uncollapseAdvancedOptions()

        then:
        powerShellTaskConfiguration.environmentVariable.value() == ""
        powerShellTaskConfiguration.getSelectedText(powerShellTaskConfiguration.bitness)  == "Any"
        powerShellTaskConfiguration.getSelectedText(powerShellTaskConfiguration.loadUserProfile)  == "Default"

    }
}