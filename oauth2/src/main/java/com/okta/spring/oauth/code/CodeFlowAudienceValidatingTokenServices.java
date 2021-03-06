/*
 * Copyright 2017 Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.spring.oauth.code;

import com.okta.spring.oauth.OAuth2AccessTokenValidationException;
import org.springframework.security.jwt.crypto.sign.InvalidSignatureException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

public class CodeFlowAudienceValidatingTokenServices extends DefaultTokenServices {

    private final String audience;

    public CodeFlowAudienceValidatingTokenServices(String audience) {
        this.audience = audience;
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessTokenValue) {
        try {
            OAuth2Authentication originalOAuth = super.loadAuthentication(accessTokenValue);

            // validate audience
            if (!originalOAuth.getOAuth2Request().getResourceIds().contains(audience)) {
                throw new OAuth2AccessTokenValidationException("Invalid token, 'aud' claim does not contain the expected audience of: " + audience);
            }

            return originalOAuth;

        } catch(InvalidSignatureException e) {
            throw new OAuth2AccessTokenValidationException("Invalid token, invalid signature", e);
        }
    }
}