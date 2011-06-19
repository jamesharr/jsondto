package org.grickle.jsondto.client;

/**
 * Generic unpickle exception with a message.
 */
public class UnpickleException extends RuntimeException
{
    public UnpickleException(String msg)
    {
        super(msg);
    }

    private static final long serialVersionUID = 1L;
}
