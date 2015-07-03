/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package signature;

/**
 * The main entry point for the signature tool. If invoked with
 * <code>--create</code> the call is delegated to
 * {@link signature.converter.Main}. If invoked with <code>--compare</code> the
 * call is delegated to {@link signature.compare.Main}.
 * 
 * @see signature.converter.Main
 * @see signature.compare.Main
 */
public class Main {

    private static final String USAGE_MESSAGE = "usage:\n"
            + "  sig --create (doclet | dex) sourcefiles --out directory"
            + " --name <name> --packages packageName{ packageName}\n"
            + "  sig --compare --from=(doclet | dex | sig) <sourcefiles>"
            + " --name <name> --to=(doclet | dex | sig) <sourcefiles>"
            + " --name <name> --out directory"
            + " --packages packageName{ packageName}\n  sig --help";

    /**
     * This class is uninstantiable.
     */
    private Main() {
        // This space intentionally left blank.
    }

    /**
     * Run!
     */
    public static void main(String[] args) {
        boolean gotCmd = false;
        boolean showUsage = false;

        try {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.equals("--") || !arg.startsWith("--")) {
                    gotCmd = false;
                    showUsage = true;
                    break;
                }

                gotCmd = true;
                if (arg.equals("--create")) {
                    signature.converter.Main.main(without(args, i));
                    break;
                } else if (arg.equals("--compare")) {
                    signature.compare.Main.main(without(args, i));
                    break;
                } else if (arg.equals("--version")) {
                    version();
                    break;
                } else if (arg.equals("--help")) {
                    showUsage = true;
                    break;
                } else {
                    gotCmd = false;
                }
            }
        } catch (UsageException ex) {
            showUsage = true;
        } catch (RuntimeException ex) {
            System.err.println("\nUNEXPECTED TOP-LEVEL EXCEPTION:");
            ex.printStackTrace();
            System.exit(2);
        } catch (Throwable ex) {
            System.err.println("\nUNEXPECTED TOP-LEVEL ERROR:");
            ex.printStackTrace();
            System.exit(3);
        }

        if (!gotCmd) {
            System.err.println("error: no command specified");
            showUsage = true;
        }

        if (showUsage) {
            usage();
            System.exit(1);
        }
    }

    /**
     * Prints the version message.
     */
    private static void version() {
        System.err.println("android sigtools version " + Version.VERSION);
        System.exit(0);
    }

    /**
     * Prints the usage message.
     */
    private static void usage() {
        System.err.println(USAGE_MESSAGE);
    }

    /**
     * Returns a copy of the given argument array, but without the indicated
     * element.
     * 
     * @param orig
     *            non-null; original array
     * @param n
     *            which element to omit
     * @return non-null; new array
     */
    private static String[] without(String[] orig, int n) {
        int len = orig.length - 1;
        String[] newa = new String[len];
        System.arraycopy(orig, 0, newa, 0, n);
        System.arraycopy(orig, n + 1, newa, n, len - n);
        return newa;
    }

}
