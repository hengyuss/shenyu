package org.apache.shenyu.plugin.record.handler;

import org.apache.shenyu.common.dto.PluginData;
import org.apache.shenyu.common.dto.RuleData;
import org.apache.shenyu.common.dto.SelectorData;
import org.apache.shenyu.common.enums.PluginEnum;
import org.apache.shenyu.plugin.base.handler.PluginDataHandler;

public class RecordPluginDataHandler implements PluginDataHandler {
    @Override
    public String pluginNamed() {
        return PluginEnum.RECORD.getName();
    }

    @Override
    public void handlerPlugin(PluginData pluginData) {
        PluginDataHandler.super.handlerPlugin(pluginData);
    }

    @Override
    public void removePlugin(PluginData pluginData) {
        PluginDataHandler.super.removePlugin(pluginData);
    }

    @Override
    public void handlerSelector(SelectorData selectorData) {
        PluginDataHandler.super.handlerSelector(selectorData);
    }

    @Override
    public void removeSelector(SelectorData selectorData) {
        PluginDataHandler.super.removeSelector(selectorData);
    }

    @Override
    public void handlerRule(RuleData ruleData) {
        PluginDataHandler.super.handlerRule(ruleData);
    }

    @Override
    public void removeRule(RuleData ruleData) {
        PluginDataHandler.super.removeRule(ruleData);
    }
}
