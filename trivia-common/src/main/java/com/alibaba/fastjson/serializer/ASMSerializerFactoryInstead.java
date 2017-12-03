/** Created by Jack Chen at 12/14/2014 */
package com.alibaba.fastjson.serializer;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONType;
import com.alibaba.fastjson.asm.ClassWriter;
import com.alibaba.fastjson.asm.Label;
import com.alibaba.fastjson.asm.MethodVisitor;
import com.alibaba.fastjson.util.ASMClassLoader;
import com.alibaba.fastjson.util.ASMUtils;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * 用于替代 ASMSerializerFactory 实现
 * (暂未使用)
 * @author Jack Chen
 */
public class ASMSerializerFactoryInstead extends ASMSerializerFactory {
//    private ASMClassLoader classLoader = null;
//
//    public ObjectSerializer createJavaBeanSerializer(Class<?> clazz, Map<String, String> aliasMap) throws Exception {
//        if(clazz.isPrimitive()) {
//            throw new JSONException("unsupportd class " + clazz.getName());
//        }
//
//        List<FieldInfo> getters = TypeUtils.computeGetters(clazz, aliasMap, false);
//
//        for(FieldInfo getter : getters) {
//            if(!ASMUtils.checkName(getter.getMember().getName())) {
//                return null;
//            }
//        }
//
//        String className = getGenClassName(clazz);
//        int beanSerializeFeatures = TypeUtils.getSerializeFeatures(clazz);
//
//        ClassWriter cw = new ClassWriter();
//
//        //此处继承特定的标记接口
//        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, className, "java/lang/Object",
//                new String[] {"com/alibaba/fastjson/serializer/ObjectSerializer", "com/yunat/newbi/common/component/json/asm/AsmSerializer"});
//
//        {
//            FieldVisitor fw = cw.visitField(ACC_PRIVATE, "nature", "Lcom/alibaba/fastjson/serializer/JavaBeanSerializer;");
//            fw.visitEnd();
//        }
//
//        for(FieldInfo fieldInfo : getters) {
//            {
//                FieldVisitor fw = cw.visitField(ACC_PUBLIC, fieldInfo.getName() + "_asm_fieldPrefix", "Ljava/lang/reflect/Type;");
//                fw.visitEnd();
//            }
//
//            FieldVisitor fw = cw.visitField(ACC_PUBLIC, fieldInfo.getName() + "_asm_fieldType", "Ljava/lang/reflect/Type;");
//            fw.visitEnd();
//        }
//
//        MethodVisitor mw = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
//        mw.visitVarInsn(ALOAD, 0);
//        mw.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
//
//        // mw.visitFieldInsn(PUTFIELD, context.getClassName(), fieldInfo.getName() + "_asm_prefix__", "[C");
//
//        for(FieldInfo fieldInfo : getters) {
//            mw.visitVarInsn(ALOAD, 0);
//
//            mw.visitLdcInsn(com.alibaba.fastjson.asm.Type.getType(getDesc(fieldInfo.getDeclaringClass())));
//
//            if(fieldInfo.getMethod() != null) {
//                mw.visitLdcInsn(fieldInfo.getMethod().getName());
//                mw.visitMethodInsn(INVOKESTATIC, "com/alibaba/fastjson/util/ASMUtils", "getMethodType", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Type;");
//
//            } else {
//                mw.visitLdcInsn(fieldInfo.getField().getName());
//                mw.visitMethodInsn(INVOKESTATIC, "com/alibaba/fastjson/util/ASMUtils", "getFieldType", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/reflect/Type;");
//            }
//
//            mw.visitFieldInsn(PUTFIELD, className, fieldInfo.getName() + "_asm_fieldType", "Ljava/lang/reflect/Type;");
//        }
//
//        mw.visitInsn(RETURN);
//        mw.visitMaxs(4, 4);
//        mw.visitEnd();
//
//        {
//            Context context = new Context(className, beanSerializeFeatures);
//
//            mw = cw.visitMethod(ACC_PUBLIC, "write", "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/reflect/Type;)V", null,
//                    new String[] {"java/io/IOException"});
//
//            mw.visitVarInsn(ALOAD, context.serializer()); // serializer
//            mw.visitMethodInsn(INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "getWriter", "()Lcom/alibaba/fastjson/serializer/SerializeWriter;");
//            mw.visitVarInsn(ASTORE, context.var("out"));
//
//            JSONType jsonType = clazz.getAnnotation(JSONType.class);
//
//            if(jsonType == null || jsonType.alphabetic()) {
//                Label _else = new Label();
//
//                mw.visitVarInsn(ALOAD, context.var("out"));
//                mw.visitFieldInsn(GETSTATIC, "com/alibaba/fastjson/serializer/SerializerFeature", "SortField", "Lcom/alibaba/fastjson/serializer/SerializerFeature;");
//                mw.visitMethodInsn(INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/SerializeWriter", "isEnabled", "(Lcom/alibaba/fastjson/serializer/SerializerFeature;)Z");
//
//                mw.visitJumpInsn(IFEQ, _else);
//                mw.visitVarInsn(ALOAD, 0);
//                mw.visitVarInsn(ALOAD, 1);
//                mw.visitVarInsn(ALOAD, 2);
//                mw.visitVarInsn(ALOAD, 3);
//                mw.visitVarInsn(ALOAD, context.paramFieldType());
//                mw.visitMethodInsn(INVOKEVIRTUAL, className, "write1",
//                        "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/reflect/Type;)V");
//                mw.visitInsn(RETURN);
//
//                mw.visitLabel(_else);
//            }
//
//            mw.visitVarInsn(ALOAD, context.obj()); // obj
//            mw.visitTypeInsn(CHECKCAST, getType(clazz)); // serializer
//            mw.visitVarInsn(ASTORE, context.var("entity")); // obj
//            generateWriteMethod(clazz, mw, getters, context);
//            mw.visitInsn(RETURN);
//            mw.visitMaxs(5, context.getVariantCount() + 1);
//            mw.visitEnd();
//        }
//
//        List<FieldInfo> sortedGetters = TypeUtils.computeGetters(clazz, aliasMap, true);
//        {
//
//            // sortField support
//            Context context = new Context(className, beanSerializeFeatures);
//
//            mw = cw.visitMethod(ACC_PUBLIC, "write1", "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/reflect/Type;)V", null,
//                    new String[] {"java/io/IOException"});
//
//            mw.visitVarInsn(ALOAD, context.serializer()); // serializer
//            mw.visitMethodInsn(INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "getWriter", "()Lcom/alibaba/fastjson/serializer/SerializeWriter;");
//            mw.visitVarInsn(ASTORE, context.var("out"));
//
//            mw.visitVarInsn(ALOAD, context.obj()); // obj
//            mw.visitTypeInsn(CHECKCAST, getType(clazz)); // serializer
//            mw.visitVarInsn(ASTORE, context.var("entity")); // obj
//
//            generateWriteMethod(clazz, mw, sortedGetters, context);
//
//            mw.visitInsn(RETURN);
//            mw.visitMaxs(5, context.getVariantCount() + 1);
//            mw.visitEnd();
//        }
//
//        // writeAsArray
//        {
//            Context context = new Context(className, beanSerializeFeatures);
//
//            mw = cw.visitMethod(ACC_PUBLIC, "writeAsArray", "(Lcom/alibaba/fastjson/serializer/JSONSerializer;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/reflect/Type;)V", null,
//                    new String[] {"java/io/IOException"});
//
//            mw.visitVarInsn(ALOAD, context.serializer()); // serializer
//            mw.visitMethodInsn(INVOKEVIRTUAL, "com/alibaba/fastjson/serializer/JSONSerializer", "getWriter", "()Lcom/alibaba/fastjson/serializer/SerializeWriter;");
//            mw.visitVarInsn(ASTORE, context.var("out"));
//
//            mw.visitVarInsn(ALOAD, context.obj()); // obj
//            mw.visitTypeInsn(CHECKCAST, getType(clazz)); // serializer
//            mw.visitVarInsn(ASTORE, context.var("entity")); // obj
//            generateWriteAsArray(clazz, mw, sortedGetters, context);
//            mw.visitInsn(RETURN);
//            mw.visitMaxs(5, context.getVariantCount() + 1);
//            mw.visitEnd();
//        }
//
//        byte[] code = cw.toByteArray();
//        //
//        // org.apache.commons.io.IOUtils.write(code, new java.io.FileOutputStream(
//        // "/usr/alibaba/workspace-3.7/fastjson-asm/target/classes/"
//        // + className + ".class"));
//
//        FileCopyUtils.copy(code, new File("/tmp/" + className + ".class"));
//
//        Class<?> exampleClass = classLoader.defineClassPublic(className, code, 0, code.length);
//        Object instance = exampleClass.newInstance();
//
//        return (ObjectSerializer) instance;
//    }
//
//    private void generateWriteMethod(Class<?> clazz, MethodVisitor mw, List<FieldInfo> getters, Context context) throws Exception {
//    }
//
//    private void generateWriteAsArray(Class<?> clazz, MethodVisitor mw, List<FieldInfo> getters, Context context) throws Exception {
//    }


}
