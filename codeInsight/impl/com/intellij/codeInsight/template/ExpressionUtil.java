 package com.intellij.codeInsight.template;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.SuggestedNameInfo;
import com.intellij.psi.codeStyle.VariableKind;
import com.intellij.psi.text.BlockSupport;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nullable;

 /**
  * @author mike
  */
 public class ExpressionUtil {
   private static final Logger LOG = Logger.getInstance("#com.intellij.codeInsight.template.ExpressionUtil");

   private ExpressionUtil() {
   }

   @Nullable
   public static String[] getNames(final ExpressionContext context) {
     Project project = context.getProject();
     int offset = context.getStartOffset();

     PsiDocumentManager.getInstance(project).commitAllDocuments();

     String[] names = null;
     PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(context.getEditor().getDocument());
     PsiElement element = file.findElementAt(offset);
     if (element instanceof PsiIdentifier){
       names = getNamesForIdentifier(project, (PsiIdentifier)element);
     }
     else{
       PsiFile fileCopy = (PsiFile)file.copy();
       BlockSupport blockSupport = project.getComponent(BlockSupport.class);
       try{
         blockSupport.reparseRange(fileCopy, offset, offset, "xxx");
       }
       catch(IncorrectOperationException e){
         LOG.error(e);
       }
       PsiElement identifierCopy = fileCopy.findElementAt(offset);
       if (identifierCopy instanceof PsiIdentifier) {
         names = getNamesForIdentifier(project, (PsiIdentifier)identifierCopy);
       }
     }
     return names;
   }

   @Nullable
   private static String[] getNamesForIdentifier(Project project, PsiIdentifier identifier){
     if (identifier.getParent() instanceof PsiVariable){
       PsiVariable var = (PsiVariable)identifier.getParent();
       if (identifier.equals(var.getNameIdentifier())){
         CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
         VariableKind variableKind = codeStyleManager.getVariableKind(var);
         PsiExpression initializer = var.getInitializer();
         if (var instanceof PsiParameter && ((PsiParameter)var).getDeclarationScope() instanceof PsiForeachStatement) {
           //synthesize initializer
           PsiForeachStatement foreachStatement = (PsiForeachStatement)((PsiParameter)var).getDeclarationScope();
           final PsiExpression iteratedValue = foreachStatement.getIteratedValue();
           if (iteratedValue != null) {
             try {
               final PsiArrayAccessExpression expr = (PsiArrayAccessExpression)iteratedValue.getManager().getElementFactory().createExpressionFromText("a[0]", var);
               expr.getArrayExpression().replace(iteratedValue);
               initializer = expr; //note: non physical with no parent
             }
             catch (IncorrectOperationException e) {
               //do nothing
             }
           }
         }
         SuggestedNameInfo suggestedInfo = codeStyleManager.suggestVariableName(variableKind, null, initializer, var.getType());
         return suggestedInfo.names;
       }
     }
     return null;
   }
 }
