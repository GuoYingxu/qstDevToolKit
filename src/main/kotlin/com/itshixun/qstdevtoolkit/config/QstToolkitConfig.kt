package com.itshixun.qstdevtoolkit.config

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.util.NlsContexts.ConfigurableName
import com.intellij.ui.dsl.builder.panel
import com.itshixun.qstdevtoolkit.service.QstToolkitConfigService
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.JComponent
import javax.swing.JTextField
import kotlin.reflect.KMutableProperty0

class QstToolkitConfig: Configurable {
    private var component: JComponent? = null
    companion object {
        var HW_ACCESS_KEY = "hwAccessKey"
        var HW_SECRET_KEY = "hwSecretKey"
        var HW_PROJECT_IDS = "hwProjectIds"
        var HW_PROJECT_ASSIGNED_USER = "hwProjectAssignedUser"
        var REST_API_HOST = "restApiHost"
    }
//    private val keyHint = "AK"
//    private val secretHint = "SK"
//    private val projectHint = "请输入华为云项目ID(用逗号分割)"
//    private val assignedUserHint = "请输入任务处理人num_id(用逗号分割)"
    private val configData:ConfigData
    data class ConfigData(var hwAccessKey:String?,var hwSecretKey: String?,var hwProjectIds:String?,var hwProjectAssignedUser:String?,var restApiHost:String?)


    var configService: QstToolkitConfigService? = ApplicationManager.getApplication().getService<QstToolkitConfigService>(QstToolkitConfigService::class.java)
    init {
        configData = ConfigData(
            configService!![HW_ACCESS_KEY, ""],
            configService!![HW_SECRET_KEY, ""],
            configService!![HW_PROJECT_IDS, ""],
            configService!![HW_PROJECT_ASSIGNED_USER, ""],
            configService!![REST_API_HOST, ""]
        )
        component = panel {
            group("华为云配置") {

                row {
                    label("华为云访问密钥AK:")
                }
                row {
                    textField().applyToComponent {
                        this.text = configData.hwAccessKey
                        this.addFocusListener(
                                TextFieldListener(this, configData::hwAccessKey )
                        )
                    }
                }
                row {
                    label("华为云访问密钥SK:")
                }
                row {
                    textField().applyToComponent {
                        this.text = configData.hwSecretKey
                        this.addFocusListener(
                                TextFieldListener(this, configData::hwSecretKey )
                        )
                    }
                }
                row {
                    label("华为云项目IDs:")
                }
                row {
                    textField().applyToComponent {
                        this.text = configData.hwProjectIds
                        this.addFocusListener(
                                TextFieldListener(this, configData::hwProjectIds )
                        )
                    }
                }
                row {
                    label("华为云项目任务处理人:")
                }
                row {
                    textField().applyToComponent {
                        this.text = configData.hwProjectAssignedUser
                        this.addFocusListener(
                                TextFieldListener(this, configData::hwProjectAssignedUser )
                        )
                    }
                }
            }
            group("RestApiConfig") {
                row("API服务HOST:"){
                    textField().applyToComponent {
                        this.text = configData.restApiHost
                        this.addFocusListener(
                                TextFieldListener(this, configData::restApiHost )
                        )
                    }
                }
            }
        }
    }

    override fun getDisplayName(): @ConfigurableName String {
        return "QstToolkitConfiguration"
    }

    override fun isModified(): Boolean {
        return true
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
        configService!!.save(HW_ACCESS_KEY, this.configData.hwAccessKey)
        configService!!.save(HW_SECRET_KEY, this.configData.hwSecretKey)
        configService!!.save(HW_PROJECT_IDS, this.configData.hwProjectIds)
        configService!!.save(HW_PROJECT_ASSIGNED_USER, this.configData.hwProjectAssignedUser)
        configService!!.save(REST_API_HOST, this.configData.restApiHost)
    }

    override fun createComponent(): JComponent? {
        return component
    }

    internal class TextFieldListener(private val textField: JTextField, private var setter: KMutableProperty0<String?>) :
        FocusListener {
        override fun focusGained(e: FocusEvent) {
//            if (textField!!.text == defaultHint) {
//                textField.text = ""
//                textField.foreground = JBColor.WHITE
//            }
        }

        override fun focusLost(e: FocusEvent) {
//            println("::::::${textField!!.text}")
            setter.set(textField.text)
//            if (textField!!.text == "") {
//                textField.text = defaultHint
//                textField.foreground = JBColor.GRAY
//            } else {
//                textField.foreground = JBColor.WHITE
//            }
        }
    }
}


