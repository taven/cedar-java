/*
 * Copyright 2022-2023 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cedarpolicy.value;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Represents a Cedar Entity UID. An entity UID contains both the entity type and a unique
 * identifier for the entity formatted as <code>TYPE::"ID"</code>.
 */
public class EntityUID extends Value {

    private static class EUIDValidator {
        // Any char except {'\','"'} or escaped slash `\\` or escaped quote `\"` or unicode `\u0000`
        // We need to escape twice, once for Java and once for the Regex
        private static final String anyCharExceptSlashOrQuote = "[^\\\"\\\\]";
        private static final String escapedSlash = "\\\\\\\\";
        private static final String escapedQuote = "\\\\\\\"";
        private static final String unicodeEscapedPattern = "\\\\u[A-Fa-f0-9]{4,6}";
        private static final Pattern entityUIDPattern =
                Pattern.compile(
                        "^([A-Za-z_]([A-Za-z0-9_])*::)+\"("
                                + anyCharExceptSlashOrQuote
                                + "|"
                                + escapedSlash
                                + "|"
                                + escapedQuote
                                + "|"
                                + unicodeEscapedPattern
                                + ")*\"$");
        private static final long MAXLENGTH = 1024;

        public static boolean validEntityUID(String id) {
            if (id == null || id.isEmpty()) return false;
            id = id.trim();
            if (id.length() > MAXLENGTH) return false;
            try {
                Matcher matcher = entityUIDPattern.matcher(id);
                return matcher.matches();
            } catch (PatternSyntaxException ex) {
                return false;
            }
        }
    }

    /** Entity uid. */
    private final String euid;

    /** Entity uid type. */
    private final Optional<String> type;

    /** Entity uid type. */
    private final Optional<String> id;


    /**
     * Build EntityUID.
     *
     * @param euid Entity Unique ID as a string.
     *     <p>Note, we limit euids to 1024 chars.
     */
    public EntityUID(String euid) throws IllegalArgumentException {
        if (!EUIDValidator.validEntityUID(euid)) {
            throw new IllegalArgumentException("Input string is not a valid EntityUID " + euid);
        }

        this.euid = euid;
        this.type = Optional.empty();
        this.id = Optional.empty();
    }

    /**
     * Build EntityUID.
     *
     * @param type Type component of Entity Unique ID.
     * @param id Id component of Entity Unique ID.
     *     <p>Note, we limit euids to 1024 chars.
     */
    public EntityUID(String type, String id) throws IllegalArgumentException {
        String euid = type + "::\""+id+"\"";
        if (!EUIDValidator.validEntityUID(euid)) {
            throw new IllegalArgumentException("Input string is not a valid EntityUID " + euid);
        }
        this.euid = euid;
        this.type = Optional.of(type);
        this.id = Optional.of(id);
    }

    /** As String. */
    @Override
    public String toString() {
        return euid;
    }

    /** To Cedar expr that can be used in a Cedar policy. */
    @Override
    public String toCedarExpr() {
        return euid;
    }

    /** Get the type of the EUID. */
    public String getType() {
        if(!this.type.isEmpty()) {
            return this.type.get();
        }
        return this.euid.substring(0, this.euid.length()-this.getId().length()-4);
    }

    /** Get the ID of the EUID. */
    public String getId() {
        if(!this.id.isEmpty()) {
            return this.id.get();
        }
        String[] strs = this.euid.split("::");
        return strs[strs.length-1].substring(1,strs[strs.length-1].length()-1);
    }
}
