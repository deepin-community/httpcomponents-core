/*
 * ====================================================================
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
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.http.nio.reactor;

import java.nio.channels.SelectionKey;

/**
 * Type of I/O event notifications I/O sessions can declare interest in.
 *
 * @since 4.0
 */
public interface EventMask {

    /**
     * Interest in data input.
     */
    int READ = SelectionKey.OP_READ;

    /**
     * Interest in data output.
     */
    int WRITE = SelectionKey.OP_WRITE;

    /**
     * Interest in data input/output.
     */
    int READ_WRITE = READ | WRITE;

}
