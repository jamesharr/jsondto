package org.grickle.rebind;

import java.io.PrintWriter;

import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Generates front-end Pickler<T> implementation code.
 */
public class PicklerGenerator
{
    private static final String JSONVALUE_CLS = "com.google.gwt.json.client.JSONValue";

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

        // Generate class
        src.println("public " + JSONVALUE_CLS + " pickle(" + pickledType.getParameterizedQualifiedSourceName() + " obj)");
        src.println("{");
        src.indentln("return " + staticPickler + ".pickle(obj);");
        src.println("}");

        src.println("public " + pickledType.getParameterizedQualifiedSourceName() + " unpickle(" + JSONVALUE_CLS + " json)");
        src.println("{");
        src.indentln("return " + staticPickler + ".unpickle(json);");
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

        // Get the two methods we should have
        JMethod pickleMethod = null, unpickleMethod = null;
        for (JMethod m : picklerIface.getOverridableMethods())
        {
            logger.log(TreeLogger.DEBUG, "Found method:" + m.getReadableDeclaration());
            if ( m.getName().equals("pickle") )
                pickleMethod = m;
            else if ( m.getName().equals("unpickle") )
                unpickleMethod = m;
            else
                fail("Unknown method signature in pickler.");
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

        // check unpickle method signature.
        if ( unpickleMethod == null )
            fail("Could not find unpickle method");
        if ( unpickleMethod.getReturnType() != pickleType )
            fail("Unpickle doesn't return our pickle type.");
        if ( unpickleMethod.getParameters().length != 1 )
            fail("unpickle() argument count is invalid");
        if ( unpickleMethod.getParameters()[0].getType() != jsonValue )
            fail("unpickle doesn't take a JSONValue");

        return pickleType;
    }

    private void fail(String msg) throws UnableToCompleteException
    {
        logger.log(TreeLogger.ERROR, msg);
        throw new UnableToCompleteException();
    }
}
