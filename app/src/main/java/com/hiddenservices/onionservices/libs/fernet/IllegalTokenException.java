/**
 * Copyright 2017 Carlos Macasaet
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hiddenservices.onionservices.libs.fernet;

/**
 * This exception indicates that a Fernet token could not be created because one or more of the parameters was invalid.
 *
 * <p>Copyright &copy; 2017 Carlos Macasaet.</p>
 *
 * @author Carlos Macasaet
 */
public class IllegalTokenException extends IllegalArgumentException {

    private static final long serialVersionUID = -1794971941479648725L;

    public IllegalTokenException(final String message) {
        super(message);
    }

    public IllegalTokenException(final String message, final Throwable cause) {
        super(message, cause);
    }

}