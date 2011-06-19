package org.grickle.jsondto.rebind;

import java.io.PrintWriter;

import org.grickle.jsondto.client.RpcEndpoint;
import org.grickle.jsondto.client.RpcHTTPUtil;
import org.grickle.jsondto.client.RpcMethodMap;
import org.grickle.jsondto.client.RpcRequestType;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.JParameterizedType;
import com.google.gwt.core.ext.typeinfo.JType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Generates an implementation of JSONRemoteService class.
 */
public class JsonRpcServiceGenerator extends Generator
{
    private static final String ASYNC_CALLBACK = AsyncCallback.class.getName();
    private static final String JSONARRAY = JSONArray.class.getName();
    private static final String JSONVALUE = JSONValue.class.getName();

    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName)
    throws UnableToCompleteException
    {
        JClassType svcIface = getSvcIface(logger, context, typeName);
        SourceWriter src = getWriter(logger, context, svcIface);
        writeSource(logger, context, src, svcIface);
        return NameMangler.getPackageName(svcIface) + "." + NameMangler.getServiceImplName(svcIface);
    }

    /**
     * @param logger
     * @param context
     * @param typeName
     * @return
     * @throws UnableToCompleteException if class is not locatable or is not an interface
     */
    private JClassType getSvcIface (TreeLogger logger, GeneratorContext context, String typeName)
    throws UnableToCompleteException
    {
        JClassType rv = null;
        JType type = getType(logger, context, typeName);
        rv = type.isInterface();
        if ( rv == null )
            fail(logger, "Service interface to be an interface: " + typeName);
        return rv;
    }

    /**
     * Get a SourceWriter for the service interface class
     * 
     * @param logger
     * @param context
     * @param serviceIface
     * @return Writer for a class, or null if it already exists
     */
    private SourceWriter getWriter(TreeLogger logger, GeneratorContext context, JClassType serviceIface)
    {
        String pkg = NameMangler.getPicklerPackageName(serviceIface);
        String implName = NameMangler.getPicklerImplName(serviceIface);
        ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(pkg, implName);
        composerFactory.addImplementedInterface(serviceIface.getParameterizedQualifiedSourceName());
        logger.log(TreeLogger.DEBUG, "Creating class " + pkg + "." + implName);
        PrintWriter printWriter = context.tryCreate(logger, pkg, implName);
        if ( printWriter == null )
            return  null;
        SourceWriter src = composerFactory.createSourceWriter(context, printWriter);
        return src;
    }

    /**
     * Write the implementation for a service unless src is null
     * 
     * @param logger
     * @param context
     * @param src
     * @param svcIface
     * @throws UnableToCompleteException
     */
    private void writeSource(TreeLogger logger, GeneratorContext context, SourceWriter src, JClassType svcIface)
    throws UnableToCompleteException
    {
        RpcEndpoint bindAnnotation = svcIface.getAnnotation(RpcEndpoint.class);
        if ( bindAnnotation == null )
            fail(logger, "Unable to find " + RpcEndpoint.class.getName() + " annotation on class");

        if ( src == null )
            return;

        for(JMethod m : svcIface.getMethods())
        {
            String methodName = m.getName();
            JParameter[] parameters = getParameters(logger, context, m);
            JType returnType = getReturnType(logger, context, m);

            // Write source
            writeMethodPrototype(logger, src, methodName, parameters, returnType);
            src.println("{");
            src.indent();
            writePickleArguments(logger, context, src, parameters);
            String rpcMethodName = getRpcMethodName(logger, svcIface, m, bindAnnotation);
            writeHTTPCall(logger, context, src, rpcMethodName, bindAnnotation, returnType);
            src.outdent();
            src.println("}");
        }
        src.commit(logger);
    }

    private String getRpcMethodName(TreeLogger logger, JClassType svcIface, JMethod m, RpcEndpoint bindAnnotation) throws UnableToCompleteException
    {
        if (bindAnnotation.methodMapping() == RpcMethodMap.FULL_CLASS_NAME)
            return svcIface.getParameterizedQualifiedSourceName() + "." + m.getName();
        else if (bindAnnotation.methodMapping() == RpcMethodMap.SHORT_CLASS)
            return svcIface.getSimpleSourceName() + "." + m.getName();
        else if ( bindAnnotation.methodMapping() == RpcMethodMap.METHOD_ONLY )
            return m.getName();
        else
            fail(logger, "Unable to determine method name type.");
        return null;
    }

    /**
     * Get parameter list without the last callback
     * 
     * @param logger
     * @param context
     * @param method
     * @return
     * @throws UnableToCompleteException
     */
    private JParameter[] getParameters(TreeLogger logger, GeneratorContext context, JMethod method)
    throws UnableToCompleteException
    {
        JClassType asyncCallbackType = getType(logger, context, ASYNC_CALLBACK).isInterface();
        JParameter[] params = method.getParameters();
        if ( params.length == 0 )
            return params;

        JClassType last = params[params.length-1].getType().isClassOrInterface();
        if ( last != null && last.isAssignableTo(asyncCallbackType))
        {
            JParameter[] rv = new JParameter[params.length-1];
            for(int i=0; i<rv.length; ++i)
                rv[i] = params[i];
            return rv;
        }
        else
        {
            return params;
        }
    }

    /*
     * Get the return type of the last callback (if any)
     */
    private JType getReturnType(TreeLogger logger, GeneratorContext context, JMethod method) throws UnableToCompleteException
    {
        JClassType asyncCallbackType = getType(logger, context, ASYNC_CALLBACK).isInterface();
        JType[] params = method.getParameterTypes();
        if ( params.length > 0)
        {
            JType last = params[params.length-1];
            assert(last != null);
            JParameterizedType pType = last.isParameterized();

            if ( pType.isAssignableTo(asyncCallbackType) );
            {
                JType[] args = pType.getTypeArgs();
                assert(args.length == 1);
                return args[0];
            }
        }
        return null;
    }

    private void writeMethodPrototype(TreeLogger logger, SourceWriter src, String methodName,
            JParameter[] parameters, JType returnType)
    {
        src.println("void " + methodName + "(");
        src.indent();

        // Arguments as arg0, arg1, arg2, etc
        for(int i=0; i<parameters.length; ++i)
        {
            String paramSrc = parameters[i].getType().getParameterizedQualifiedSourceName() + " arg" + i;
            if ( i+1 != parameters.length || returnType != null )
                paramSrc += ",";
            src.println(paramSrc);
        }

        // Callback if any
        if ( returnType != null )
            src.println("final " + ASYNC_CALLBACK + "<" + returnType.getParameterizedQualifiedSourceName() + "> callback");

        src.outdent();
        src.println(")");
    }

    private void writePickleArguments(TreeLogger logger, GeneratorContext context, SourceWriter src, JParameter[] parameters)
    throws UnableToCompleteException
    {
        StaticPicklerFactory picklerFactory = StaticPicklerFactory.getInstance();

        src.println(JSONARRAY + " args = new " + JSONARRAY + "();");
        int argc = 0;
        for(JParameter p : parameters)
        {
            String pickler = picklerFactory.getPickler(logger, context, p.getType());
            src.println("// " + p.getName());
            src.println("args.set(" + argc + "," + pickler + ".pickle(arg" + argc + "));");
            argc++;
        }
    }

    private void writeHTTPCall(TreeLogger logger, GeneratorContext context, SourceWriter src, String rpcMethodName, RpcEndpoint url, JType returnType) throws UnableToCompleteException
    {
        String method = getHTTPUtilMethodName(logger, url.requestMethod());
        String pickler = StaticPicklerFactory.getInstance().getPickler(logger, context, returnType);

        src.println(RpcHTTPUtil.class.getName() + "." + method + "(" + ", ");
        src.println("new " + ASYNC_CALLBACK + "<" + JSONVALUE + "(){");
        src.indent();

        // Success case
        src.println("public void onSuccess(" + JSONVALUE + " json) {");
        src.indentln("try { callback.onSuccess(" + pickler + ".unpickle(json); }");
        src.indentln("catch(Exception e){callback.onFailure(e);}");
        src.println("}");

        // Failure case
        src.println("public void onFailure(Throwable err) {");
        src.indentln("callback.onFailure(err);");
        src.println("}");

        src.outdent();
        src.println("});");
    }

    private String getHTTPUtilMethodName(TreeLogger logger, RpcRequestType type) throws UnableToCompleteException
    {
        switch(type)
        {
        case GET_BASE64:
            return "get_base64";

        case GET_URLENCODED:
            return "get_urlencoded";

        case POST:
            return "post";

        case POST_ENCODED:
            return "post_encoded";

        default:
            fail(logger, "HOW DID YOU DO THIS?");
            return null;
        }
    }

    private JType getType(TreeLogger logger, GeneratorContext ctx, String type) throws UnableToCompleteException
    {
        try {
            return ctx.getTypeOracle().getType(type);
        }
        catch (NotFoundException e)
        {
            fail(logger, "Unable to find type " + type);
        }
        return null;
    }

    private void fail(TreeLogger logger, String msg) throws UnableToCompleteException
    {
        logger.log(TreeLogger.ERROR, msg);
        throw new UnableToCompleteException();
    }

}
