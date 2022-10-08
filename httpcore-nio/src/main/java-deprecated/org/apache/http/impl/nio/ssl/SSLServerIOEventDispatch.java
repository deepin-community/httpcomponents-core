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

package org.apache.http.impl.nio.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;

import org.apache.http.impl.nio.DefaultServerIOEventDispatch;
import org.apache.http.impl.nio.reactor.SSLIOSession;
import org.apache.http.impl.nio.reactor.SSLSetupHandler;
import org.apache.http.nio.NHttpServerIOTarget;
import org.apache.http.nio.NHttpServiceHandler;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

/**
 * Default implementation of {@link org.apache.http.nio.reactor.IOEventDispatch}
 * interface for SSL (encrypted) server-side HTTP connections.
 *
 * @since 4.1
 *
 * @deprecated (4.2) use {@link org.apache.http.impl.nio.DefaultHttpServerIODispatch}
 */
@Deprecated
public class SSLServerIOEventDispatch extends DefaultServerIOEventDispatch {

    private final SSLContext sslContext;
    private final SSLSetupHandler sslHandler;

    /**
     * Creates a new instance of this class to be used for dispatching I/O event
     * notifications to the given protocol handler using the given
     * {@link SSLContext}. This I/O dispatcher will transparently handle SSL
     * protocol aspects for HTTP connections.
     *
     * @param handler the server protocol handler.
     * @param sslContext the SSL context.
     * @param sslHandler the SSL setup handler.
     * @param params HTTP parameters.
     */
    public SSLServerIOEventDispatch(
            final NHttpServiceHandler handler,
            final SSLContext sslContext,
            final SSLSetupHandler sslHandler,
            final HttpParams params) {
        super(handler, params);
        Args.notNull(sslContext, "SSL context");
        Args.notNull(params, "HTTP parameters");
        this.sslContext = sslContext;
        this.sslHandler = sslHandler;
    }

    /**
     * Creates a new instance of this class to be used for dispatching I/O event
     * notifications to the given protocol handler using the given
     * {@link SSLContext}. This I/O dispatcher will transparently handle SSL
     * protocol aspects for HTTP connections.
     *
     * @param handler the server protocol handler.
     * @param sslContext the SSL context.
     * @param params HTTP parameters.
     */
    public SSLServerIOEventDispatch(
            final NHttpServiceHandler handler,
            final SSLContext sslContext,
            final HttpParams params) {
        this(handler, sslContext, null, params);
    }

    /**
     * Creates an instance of {@link SSLIOSession} decorating the given
     * {@link IOSession}.
     * <p>
     * This method can be overridden in a super class in order to provide
     * a different implementation of SSL I/O session.
     *
     * @param session the underlying I/O session.
     * @param sslContext the SSL context.
     * @param sslHandler the SSL setup handler.
     * @return newly created SSL I/O session.
     */
    protected SSLIOSession createSSLIOSession(
            final IOSession session,
            final SSLContext sslContext,
            final SSLSetupHandler sslHandler) {
        return new SSLIOSession(session, sslContext, sslHandler);
    }

    protected NHttpServerIOTarget createSSLConnection(final SSLIOSession sslioSession) {
        return super.createConnection(sslioSession);
    }

    @Override
    protected NHttpServerIOTarget createConnection(final IOSession session) {
        final SSLIOSession sslioSession = createSSLIOSession(session, this.sslContext, this.sslHandler);
        session.setAttribute(SSLIOSession.SESSION_KEY, sslioSession);
        final NHttpServerIOTarget conn = createSSLConnection(sslioSession);
        try {
            sslioSession.initialize();
        } catch (final SSLException ex) {
            this.handler.exception(conn, ex);
            sslioSession.shutdown();
        }
        return conn;
    }

    @Override
    public void onConnected(final NHttpServerIOTarget conn) {
        final int timeout = HttpConnectionParams.getSoTimeout(this.params);
        conn.setSocketTimeout(timeout);
        this.handler.connected(conn);
    }

}
