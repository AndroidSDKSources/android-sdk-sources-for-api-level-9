/*
 * Copyright (C) 2008 The Android Open Source Project
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

package android.renderscript;


import android.util.Config;
import android.util.Log;


/**
 * @hide
 *
 **/
public class Program extends BaseObj {
    public static final int MAX_INPUT = 8;
    public static final int MAX_OUTPUT = 8;
    public static final int MAX_CONSTANT = 8;
    public static final int MAX_TEXTURE = 8;

    Element mInputs[];
    Element mOutputs[];
    Type mConstants[];
    int mTextureCount;
    String mShader;

    Program(int id, RenderScript rs) {
        super(rs);
        mID = id;
    }

    public void bindConstants(Allocation a, int slot) {
        mRS.nProgramBindConstants(mID, slot, a.mID);
    }

    public void bindTexture(Allocation va, int slot)
        throws IllegalArgumentException {
        mRS.validate();
        if((slot < 0) || (slot >= mTextureCount)) {
            throw new IllegalArgumentException("Slot ID out of range.");
        }

        mRS.nProgramBindTexture(mID, slot, va.mID);
    }

    public void bindSampler(Sampler vs, int slot)
        throws IllegalArgumentException {
        mRS.validate();
        if((slot < 0) || (slot >= mTextureCount)) {
            throw new IllegalArgumentException("Slot ID out of range.");
        }

        mRS.nProgramBindSampler(mID, slot, vs.mID);
    }


    public static class BaseProgramBuilder {
        RenderScript mRS;
        Element mInputs[];
        Element mOutputs[];
        Type mConstants[];
        Type mTextures[];
        int mInputCount;
        int mOutputCount;
        int mConstantCount;
        int mTextureCount;
        String mShader;


        protected BaseProgramBuilder(RenderScript rs) {
            mRS = rs;
            mInputs = new Element[MAX_INPUT];
            mOutputs = new Element[MAX_OUTPUT];
            mConstants = new Type[MAX_CONSTANT];
            mInputCount = 0;
            mOutputCount = 0;
            mConstantCount = 0;
            mTextureCount = 0;
        }

        public void setShader(String s) {
            mShader = s;
        }

        public void addInput(Element e) throws IllegalStateException {
            // Should check for consistant and non-conflicting names...
            if(mInputCount >= MAX_INPUT) {
                throw new IllegalArgumentException("Max input count exceeded.");
            }
            mInputs[mInputCount++] = e;
        }

        public void addOutput(Element e) throws IllegalStateException {
            // Should check for consistant and non-conflicting names...
            if(mOutputCount >= MAX_OUTPUT) {
                throw new IllegalArgumentException("Max output count exceeded.");
            }
            mOutputs[mOutputCount++] = e;
        }

        public int addConstant(Type t) throws IllegalStateException {
            // Should check for consistant and non-conflicting names...
            if(mConstantCount >= MAX_CONSTANT) {
                throw new IllegalArgumentException("Max input count exceeded.");
            }
            mConstants[mConstantCount] = t;
            return mConstantCount++;
        }

        public void setTextureCount(int count) throws IllegalArgumentException {
            // Should check for consistant and non-conflicting names...
            if(count >= MAX_CONSTANT) {
                throw new IllegalArgumentException("Max texture count exceeded.");
            }
            mTextureCount = count;
        }

        protected void initProgram(Program p) {
            p.mInputs = new Element[mInputCount];
            System.arraycopy(mInputs, 0, p.mInputs, 0, mInputCount);
            p.mOutputs = new Element[mOutputCount];
            System.arraycopy(mOutputs, 0, p.mOutputs, 0, mOutputCount);
            p.mConstants = new Type[mConstantCount];
            System.arraycopy(mConstants, 0, p.mConstants, 0, mConstantCount);
            p.mTextureCount = mTextureCount;
        }
    }

}


