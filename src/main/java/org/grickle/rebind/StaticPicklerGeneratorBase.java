package org.grickle.rebind;

import java.io.PrintWriter;

import org.grickle.client.PickleException;
import org.grickle.client.UnpickleException;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Generate pickler
 */
public abstract class StaticPicklerGeneratorBase implements StaticPicklerGenerator
{
    StaticPicklerFactory factory;
    JType type;
    GeneratorContext context;
    TreeLogger logger;

    static final String JSONOBJECT_CLS = JSONObject.class.getName();
    static final String JSONVALUE_CLS = JSONValue.class.getName();
    static final String JSONNULL_CLS = JSONNull.class.getName();
    static final String JSONARRAY_CLS = JSONArray.class.getName();
    static final String UNPICKLE_EXCEPTION_CLS = UnpickleException.class.getName();
    static final String PICKLE_EXCEPTION_CLS = PickleException.class.getName();

    private boolean isGenerated = false;

    public StaticPicklerGeneratorBase(TreeLogger logger, GeneratorContext context, StaticPicklerFactory factory, JType type)
    {
        this.context = context;
        this.logger = logger;
        this.factory = factory;
        this.type = type;
    }

    @Override
    final public JType getPicklerType()
    {
        return type;
    }

    @Override
    final public String getPicklerClassName()
    {
        return NameMangler.getPicklerPackageName(type) + "." + NameMangler.getPicklerName(type);
    }

    @Override
    final public void generate() throws UnableToCompleteException
    {
        if ( isGenerated )
            return;

        // Need to mark as generated first in case we have a recursive type.
        isGenerated = true;
        generateJavaSourceCode();
    }

    final protected SourceWriter startClassFile(TreeLogger logger, String superClass)
    {
        String packageName = NameMangler.getPicklerPackageName(type);
        String picklerImplName = NameMangler.getPicklerName(type);
        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, picklerImplName);
        if ( superClass != null )
            composerFactory.setSuperclass(superClass);
        PrintWriter printWriter = context.tryCreate(logger, packageName, picklerImplName);
        SourceWriter src = composerFactory.createSourceWriter(context, printWriter);
        return src;
    }

    /**
     * Generate the code for the pickler.
     */
    abstract void generateJavaSourceCode() throws UnableToCompleteException;

    /**
     * @param logger
     * @param msg
     * @throws UnableToCompleteException
     */
    protected void fail(TreeLogger logger, String msg) throws UnableToCompleteException
    {
        logger.log(TreeLogger.ERROR, msg);
        throw new UnableToCompleteException();
    }
}
