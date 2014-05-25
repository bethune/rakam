package org.rakam.analysis.script.mvel;

import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.rakam.analysis.script.FieldScript;
import org.rakam.util.UnboxedMathUtils;
import org.vertx.java.core.json.JsonObject;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by buremba on 04/05/14.
 */
public class MvelFieldScript extends FieldScript {
    final boolean userData;
    private final static ParserConfiguration parserConfiguration = new ParserConfiguration();
    static {
        parserConfiguration.addPackageImport("java.util");
        parserConfiguration.addImport("time", MVEL.getStaticMethod(System.class, "currentTimeMillis", new Class[0]));
        for (Method m : UnboxedMathUtils.class.getMethods()) {
            if ((m.getModifiers() & Modifier.STATIC) > 0) {
                parserConfiguration.addImport(m.getName(), m);
            }
        }
    }
    private final Serializable script;

    public MvelFieldScript(String script) {
        super(script);
        this.script = MVEL.compileExpression(script, new ParserContext(parserConfiguration));
        userData = script.startsWith("_user.");
    }

    @Override
    public boolean requiresUser() {
        return userData;
    }

    @Override
    public String extract(JsonObject event, JsonObject user) {
        if(!userData || user==null) {
            return (String) MVEL.executeExpression(script, event);
        }else {
            for(String key : user.getFieldNames())
                event.putString(key, "_user."+user.getString(key));
            return (String) MVEL.executeExpression(script, event);
        }
    }

    @Override
    public boolean contains(JsonObject obj) {
        for(String a : obj.getFieldNames()) {
            if(fieldKey.contains(a)) return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return fieldKey;
    }
}
