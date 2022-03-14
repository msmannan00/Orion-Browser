/**
   Copyright 2017 Carlos Macasaet

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.hiddenservices.genesissearchengine.production.libs.fernet;

import com.hiddenservices.genesissearchengine.production.libs.fernet.TokenValidationException;

/**
 * This is a special case of the {@link TokenValidationException} that indicates that the Fernet token is invalid
 * because the application-defined time-to-live has elapsed. Applications can use this to communicate to the client that
 * a new Fernet must be generated, possibly by re-authenticating.
 *
 * <p>Copyright &copy; 2017 Carlos Macasaet.</p>
 *
 * @author Carlos Macasaet
 */
public class TokenExpiredException extends TokenValidationException {

    private static final long serialVersionUID = -8250681539503776783L;

    public TokenExpiredException(final String message) {
        super(message);
    }

    public TokenExpiredException(final Throwable cause) {
        this(cause.getMessage(), cause);
    }

    public TokenExpiredException(final String message, final Throwable cause) {
        super(message, cause);
    }

}