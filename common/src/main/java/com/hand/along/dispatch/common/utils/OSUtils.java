/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hand.along.dispatch.common.utils;

/**
 * <p>
 * 系统操作符类
 * </p>
 *
 */
public class OSUtils {
    private OSUtils() {
    }

    /**
     * whether is macOS
     *
     * @return true if mac
     */
    public static boolean isMacOS() {
        return getOSName().startsWith("Mac");
    }


    /**
     * whether is windows
     *
     * @return true if windows
     */
    public static boolean isWindows() {
        return getOSName().startsWith("Windows");
    }

    /**
     * get current OS name
     *
     * @return current OS name
     */
    public static String getOSName() {
        return System.getProperty("os.name");
    }


}
