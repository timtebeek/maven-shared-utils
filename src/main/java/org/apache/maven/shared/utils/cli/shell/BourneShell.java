/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.maven.shared.utils.cli.shell;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.shared.utils.Os;

/**
 * @author Jason van Zyl
 */
public class BourneShell extends Shell {

    /**
     * Create instance of BourneShell.
     */
    public BourneShell() {
        setUnconditionalQuoting(true);
        setShellCommand("/bin/sh");
        setArgumentQuoteDelimiter('\'');
        setExecutableQuoteDelimiter('\'');
        setSingleQuotedArgumentEscaped(true);
        setSingleQuotedExecutableEscaped(false);
        setQuotedExecutableEnabled(true);
    }

    /**
     * {@inheritDoc}
     */
    public String getExecutable() {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return super.getExecutable();
        }

        return quoteOneItem(super.getExecutable(), true);
    }

    /** {@inheritDoc} */
    public List<String> getShellArgsList() {
        List<String> shellArgs = new ArrayList<String>();
        List<String> existingShellArgs = super.getShellArgsList();

        if ((existingShellArgs != null) && !existingShellArgs.isEmpty()) {
            shellArgs.addAll(existingShellArgs);
        }

        shellArgs.add("-c");

        return shellArgs;
    }

    /** {@inheritDoc} */
    public String[] getShellArgs() {
        String[] shellArgs = super.getShellArgs();
        if (shellArgs == null) {
            shellArgs = new String[0];
        }

        if ((shellArgs.length > 0) && !shellArgs[shellArgs.length - 1].equals("-c")) {
            String[] newArgs = new String[shellArgs.length + 1];

            System.arraycopy(shellArgs, 0, newArgs, 0, shellArgs.length);
            newArgs[shellArgs.length] = "-c";

            shellArgs = newArgs;
        }

        return shellArgs;
    }

    /** {@inheritDoc} */
    protected String getExecutionPreamble() {
        if (getWorkingDirectoryAsString() == null) {
            return null;
        }

        String dir = getWorkingDirectoryAsString();

        return "cd " + quoteOneItem(dir, false) + " && ";
    }

    /**
     * <p>Unify quotes in a path for the Bourne Shell.</p>
     * <pre>
     * BourneShell.quoteOneItem(null)                       = null
     * BourneShell.quoteOneItem("")                         = ''
     * BourneShell.quoteOneItem("/test/quotedpath'abc")     = '/test/quotedpath'"'"'abc'
     * BourneShell.quoteOneItem("/test/quoted path'abc")    = '/test/quoted pat'"'"'habc'
     * BourneShell.quoteOneItem("/test/quotedpath\"abc")    = '/test/quotedpath"abc'
     * BourneShell.quoteOneItem("/test/quoted path\"abc")   = '/test/quoted path"abc'
     * BourneShell.quoteOneItem("/test/quotedpath\"'abc")   = '/test/quotedpath"'"'"'abc'
     * BourneShell.quoteOneItem("/test/quoted path\"'abc")  = '/test/quoted path"'"'"'abc'
     * </pre>
     *
     * @param path not null path.
     * @return the path unified correctly for the Bourne shell.
     */
    protected String quoteOneItem(String path, boolean isExecutable) {
        if (path == null) {
            return null;
        }

        return "'" + path.replace("'", "'\"'\"'") + "'";
    }
}
