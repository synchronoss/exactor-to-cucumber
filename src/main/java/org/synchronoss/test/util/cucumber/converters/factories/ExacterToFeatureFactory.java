/**
 Copyright (c) 2016, Synchronoss
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by the Synchronoss.
 4. Neither the name of the Synchronoss nor the
 names of its contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY Synchronoss ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL Synchronoss BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * */
package org.synchronoss.test.util.cucumber.converters.factories;

import org.synchronoss.test.util.cucumber.converters.Converter;
import org.synchronoss.test.util.cucumber.converters.ExactorToFeature;

public class ExacterToFeatureFactory
{
    public Converter createConverter(String path) throws Exception {

        final String[] split = path.split("/");
        final String className = split[split.length - 1].split("\\.")[0];
        final String[] splitPath = path.split("test/exactor");
        final String featureFileLocation = splitPath[0] + "test/resources/com/newbay/sal/cucumber/" + splitPath[1].split(className)[0];

        return new ExactorToFeature(className, featureFileLocation);
    }
}
