package org.grickle.rebind;

import java.io.PrintWriter;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Generates front-end Pickler<T> implementation code.
 */
public class PicklerGenerator
{
    private static final String JSONVALUE_CLS = JSONValue.class.getName();
    private static final String JSONPARSER_CLS = JSONParser.class.getName();
    private static final String STRING_CLS = String.class.getName();

    /*
     * These private variables are convenient storage to avoid always passing types around
     */
    private JClassType picklerIface;
    private GeneratorContext context;
    private JType type;
    private TreeLogger logger;

    public PicklerGenerator(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException
    {
        this.context = context;
        this.logger = logger;
        try
        {
            type = context.getTypeOracle().getType(typeName);
            picklerIface = type.isInterface();
            if ( picklerIface == null )
                fail("Type is not an interface: " + type.getParameterizedQualifiedSourceName());
            generate();
        }
        catch (NotFoundException e)
        {
            fail("Strangely unable to find type " + typeName);
        }
    }

    public String getImplName()
    {
        return NameMangler.getPicklerPackageName(picklerIface) + "."
        + NameMangler.getPicklerImplName(picklerIface);
    }

    /**
     * Generate the implementation code for this PicklerProxy (unless it's been done already)
     * 
     * @param logger
     * @throws UnableToCompleteException
     */
    private void generate() throws UnableToCompleteException
    {
        // Get supporting data
        JType pickledType = getPickledType();
        SourceWriter src = getWriter();
        if ( src == null )
        {
            logger.log(TreeLogger.TRACE, "Class already exists");
            return;
        }

        // Get static-style pickler
        StaticPicklerFactory pf = StaticPicklerFactory.getInstance();
        String staticPickler = pf.getPickler(logger, context, pickledType);

        // Generate pickle method
        src.println("public " + JSONVALUE_CLS + " pickle(" + pickledType.getParameterizedQualifiedSourceName() + " obj)");
        src.println("{");
        src.indentln("return " + staticPickler + ".pickle(obj);");
        src.println("}");

        // Generate unpickle method
        src.println("public " + pickledType.getParameterizedQualifiedSourceName() + " unpickle(" + JSONVALUE_CLS + " json)");
        src.println("{");
        src.indentln("return " + staticPickler + ".unpickle(json);");
        src.println("}");

        // Generate shortcut unpickler
        src.println("public " + pickledType.getParameterizedQualifiedSourceName() + " unpickle(" + STRING_CLS + " str)");
        src.println("{");
        src.indentln("return unpickle(" + JSONPARSER_CLS + ".parseStrict(str));");
        src.println("}");

        // Generate pickle method
        src.println("public " + STRING_CLS + " pickleToString(" + pickledType.getParameterizedQualifiedSourceName() + " obj)");
        src.println("{");
        src.indentln("return pickle(obj).toString();");
        src.println("}");

        src.commit(logger);
    }

    private SourceWriter getWriter()
    {
        String packageName = NameMangler.getPicklerPackageName(picklerIface);
        String picklerImplName = NameMangler.getPicklerImplName(picklerIface);
        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(packageName, picklerImplName);
        composerFactory.addImplementedInterface(picklerIface.getParameterizedQualifiedSourceName());
        logger.log(TreeLogger.DEBUG, "Creating class " + packageName + "." + picklerImplName);
        PrintWriter printWriter = context.tryCreate(logger, packageName, picklerImplName);
        if ( printWriter == null )
            return  null;
        SourceWriter src = composerFactory.createSourceWriter(context, printWriter);
        return src;
    }

    private JType getPickledType() throws UnableToCompleteException
    {
        logger = logger.branch(TreeLogger.TRACE, "Checking pickler interface for " +
                picklerIface.getParameterizedQualifiedSourceName());

        // Make sure we're an interface
        if (picklerIface.isInterface() == null)
            fail("Pickler '" + picklerIface.getParameterizedQualifiedSourceName() + "' needs to be an interface.");

        JMethod pickleMethod = null;
        for (JMethod m : picklerIface.getOverridableMethods())
        {
            logger.log(TreeLogger.DEBUG, "Found method:" + m.getReadableDeclaration());
            if ( m.getName().equals("pickle") )
                pickleMethod = m;
        }

        // JSONValue type
        JClassType jsonValue = context.getTypeOracle().findType(JSONVALUE_CLS);
        assert(jsonValue != null);

        // check pickle method signature
        if ( pickleMethod == null )
            fail("Could not find pickler method");
        if ( pickleMethod.getReturnType() != jsonValue )
            fail("Return type on pickler method was not a JSONValue");
        if ( pickleMethod.getParameters().length != 1 )
            fail("pickle() arguments were invalid");
        JType pickleType = pickleMethod.getParameters()[0].getType();

        return pickleType;
    }

    private void fail(String msg) throws UnableToCompleteException
    {
        logger.log(TreeLogger.ERROR, msg);
        throw new UnableToCompleteException();
    }
}
