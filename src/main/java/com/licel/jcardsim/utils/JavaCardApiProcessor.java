/*
 * Copyright 2015 Licel Corporation.
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
package com.licel.jcardsim.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Injects jCardSim’s code into Java Card Api Reference Classes
 */
public class JavaCardApiProcessor {
    
    public static void main(String args[]) throws Exception {
        File buildDir = new File(args[0]);
        if(!buildDir.exists() || !buildDir.isDirectory()){
            throw new RuntimeException("Invalid directory: "+buildDir);
        }
        proxyClass(buildDir, "com.licel.jcardsim.framework.AIDProxy", "javacard.framework.AID",false);
        proxyClass(buildDir, "com.licel.jcardsim.framework.APDUProxy", "javacard.framework.APDU",false);
        copyClass(buildDir, "com.licel.jcardsim.framework.APDUProxy$1", "javacard.framework.APDU$1");
        proxyClass(buildDir, "com.licel.jcardsim.framework.JavaCardExceptionProxy", "javacard.framework.APDUException",true);
        proxyClass(buildDir, "com.licel.jcardsim.framework.AppletProxy", "javacard.framework.Applet",false);
        proxyClass(buildDir, "com.licel.jcardsim.framework.CardExceptionProxy", "javacard.framework.CardException",false);
        proxyClass(buildDir, "com.licel.jcardsim.framework.CardRuntimeExceptionProxy", "javacard.framework.CardRuntimeException",false);
        proxyClass(buildDir, "com.licel.jcardsim.framework.JavaCardExceptionProxy", "javacard.framework.ISOException",true);
        proxyClass(buildDir, "com.licel.jcardsim.framework.JCSystemProxy", "javacard.framework.JCSystem",false);
        proxyClass(buildDir, "com.licel.jcardsim.framework.JavaCardExceptionProxy", "javacard.framework.PINException",true);
        proxyClass(buildDir, "com.licel.jcardsim.framework.JavaCardExceptionProxy", "javacard.framework.SystemException",true);
        proxyClass(buildDir, "com.licel.jcardsim.framework.JavaCardExceptionProxy", "javacard.framework.TransactionException",true);
        proxyClass(buildDir, "com.licel.jcardsim.framework.JavaCardExceptionProxy", "javacard.framework.UserException",true);
        proxyClass(buildDir, "com.licel.jcardsim.framework.UtilProxy", "javacard.framework.Util",false);
        proxyClass(buildDir, "com.licel.jcardsim.crypto.ChecksumProxy", "javacard.security.Checksum",true);
        proxyClass(buildDir, "com.licel.jcardsim.crypto.CipherProxy", "javacardx.crypto.Cipher",true);
        proxyClass(buildDir, "com.licel.jcardsim.crypto.KeyAgreementProxy", "javacard.security.KeyAgreement",true);
        proxyClass(buildDir, "com.licel.jcardsim.crypto.KeyPairImpl", "javacard.security.KeyPair",false);
        proxyClass(buildDir, "com.licel.jcardsim.crypto.KeyBuilderProxy", "javacard.security.KeyBuilder",true);
        proxyClass(buildDir, "com.licel.jcardsim.crypto.MessageDigestProxy", "javacard.security.MessageDigest",true);
        proxyClass(buildDir, "com.licel.jcardsim.crypto.RandomDataProxy", "javacard.security.RandomData",true);
        proxyClass(buildDir, "com.licel.jcardsim.crypto.SignatureProxy", "javacard.security.Signature",true);
     }
    
    public static void proxyClass(File buildDir, String proxyClassFile, String targetClassFile, boolean skipConstructor) throws IOException{
        FileInputStream fProxyClass = new FileInputStream(new File(buildDir, proxyClassFile.replace(".", File.separator)+".class"));
        FileInputStream fTargetClass = new FileInputStream(new File(buildDir, targetClassFile.replace(".", File.separator)+".class"));
        ClassReader crProxy = new ClassReader(fProxyClass);
        ClassNode cnProxy = new ClassNode();
        crProxy.accept(cnProxy, 0);
        ClassReader crTarget = new ClassReader(fTargetClass);
        ClassNode cnTarget = new ClassNode();
        crTarget.accept(cnTarget, 0);       

        ClassNode cnProxyRemapped = new ClassNode();
        HashMap<String,String> map = new HashMap();
        map.put(cnProxy.name, cnTarget.name);
        // inner classes
        for(int i=0;i<10;i++){
            map.put(cnProxy.name+"$1", cnTarget.name+"$1");
        }
        RemappingClassAdapter ra = new RemappingClassAdapter(cnProxyRemapped, new SimpleRemapper(map));
        cnProxy.accept(ra);
        
        ClassWriter cw = new ClassWriter(crTarget,0);
        MergeAdapter ma = new MergeAdapter(cw, cnProxyRemapped, skipConstructor);
        cnTarget.accept(ma);       
        fProxyClass.close();
        fTargetClass.close();
        FileOutputStream fos = new FileOutputStream(new File(buildDir, targetClassFile.replace(".", File.separator)+".class"));
        fos.write(cw.toByteArray());
        fos.close();
    }

    public static void copyClass(File buildDir, String proxyClassFile, String targetClassName) throws IOException{
        FileInputStream fProxyClass = new FileInputStream(new File(buildDir, proxyClassFile.replace(".", File.separator)+".class"));
        ClassReader crProxy = new ClassReader(fProxyClass);
        ClassNode cnProxy = new ClassNode();
        crProxy.accept(cnProxy, 0);

        ClassWriter cw = new ClassWriter(0);
        RemappingClassAdapter ra = new RemappingClassAdapter(cw, new SimpleRemapper(cnProxy.name, targetClassName.replace(".", "/")));
        cnProxy.accept(ra);
        
        fProxyClass.close();
        FileOutputStream fos = new FileOutputStream(new File(buildDir, targetClassName.replace(".", File.separator)+".class"));
        fos.write(cw.toByteArray());
        fos.close();
    }
    
    
    static class ClassAdapter extends ClassNode implements Opcodes {

        public ClassAdapter(ClassVisitor cv) {
            super(ASM4);
            this.cv = cv;
        }

        @Override
        public void visitEnd() {
            accept(cv);
        }
    }


    static class MergeAdapter extends ClassAdapter {

        private ClassNode cn;
        private String cname;
        private HashMap<String,MethodNode> cnMethods = new HashMap();
        private boolean skipConstructor;

        public MergeAdapter(ClassVisitor cv,
                ClassNode cn, boolean skipConstructor) {
            super(cv);
            this.cn = cn;
            this.skipConstructor = skipConstructor;
            for (Iterator it = cn.methods.iterator();
                    it.hasNext();) {
                MethodNode mn = (MethodNode) it.next();
                if(skipConstructor && mn.name.equals("<init>")){
                    continue;
                }
                cnMethods.put(mn.name+mn.desc, mn);
            }
        }

        @Override
        public void visit(int version, int access,
                String name, String signature,
                String superName, String[] interfaces) {
            super.visit(version, access, name,
                    signature, superName, interfaces);
            this.cname = name;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
           if(cnMethods.containsKey(name+desc)){
                return null;
            }
            System.out.println("Use original:"+cname+name+desc);
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
        
        

        @Override
        public void visitEnd() {
            for (Iterator it = cn.fields.iterator();
                    it.hasNext();) {
                ((FieldNode) it.next()).accept(this);
            }
            for (Iterator it = cn.methods.iterator();
                    it.hasNext();) {
                MethodNode mn = (MethodNode) it.next();
                if(skipConstructor && mn.name.equals("<init>")){
                    continue;
                }
                String[] exceptions
                        = new String[mn.exceptions.size()];
                mn.exceptions.toArray(exceptions);
                MethodVisitor mv
                        = cv.visitMethod(
                                mn.access, mn.name, mn.desc,
                                mn.signature, exceptions);
                mn.instructions.resetLabels();
                mn.accept(mv);/*new RemappingMethodAdapter(
                        mn.access, mn.desc, mv,
                        new SimpleRemapper(cn.name, cname)));*/
            }
            super.visitEnd();
        }
    }
}

