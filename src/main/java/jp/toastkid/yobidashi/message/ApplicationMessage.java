/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.yobidashi.message;

/**
 * Application action.
 *
 * @author Toast kid
 *
 */
public class ApplicationMessage implements Message {

    /** Command. */
    public enum Command {
        MINIMIZE, QUIT
    }

    /** This message's command. */
    private final Command command;

    /**
     * Call from internal.
     * @param c
     */
    private ApplicationMessage(final Command c) {
        this.command = c;
    }

    /**
     * Return this object's command.
     * @return
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Make QUIT message.
     * @return message object
     */
    public static ApplicationMessage makeQuit() {
        return new ApplicationMessage(Command.QUIT);
    }

    /**
     * Make MINIMIZE message.
     * @return message object
     */
    public static ApplicationMessage makeMinimize() {
        return new ApplicationMessage(Command.MINIMIZE);
    }
}
