/*
 * Copyright 2003-2006 Dave Griffith, Bas Leijdekkers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.siyeh.ig.fixes;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.JavaRefactoringActionHandlerFactory;
import com.intellij.refactoring.RefactoringActionHandler;
import com.siyeh.InspectionGadgetsBundle;
import com.siyeh.ig.InspectionGadgetsFix;
import org.jetbrains.annotations.NotNull;

public class MoveAnonymousToInnerClassFix extends InspectionGadgetsFix {

  public static final String NAME = InspectionGadgetsBundle.message(
    "move.anonymous.to.inner.quickfix");
  private final String name;

  public MoveAnonymousToInnerClassFix(String name) {
    this.name = name;
  }

  public MoveAnonymousToInnerClassFix() {
    name = NAME;
  }

  @NotNull
  @Override
  public String getFamilyName() {
    return NAME;
  }

  @Override
  @NotNull
  public String getName() {
    return name;
  }

  @Override
  public void doFix(@NotNull final Project project, ProblemDescriptor descriptor) {
    final PsiElement nameElement = descriptor.getPsiElement();
    final PsiAnonymousClass aClass =
      (PsiAnonymousClass)nameElement.getParent();
    final JavaRefactoringActionHandlerFactory factory =
      JavaRefactoringActionHandlerFactory.getInstance();
    final RefactoringActionHandler anonymousToInner =
      factory.createAnonymousToInnerHandler();
    final DataManager dataManager = DataManager.getInstance();
    final DataContext dataContext = dataManager.getDataContext();
    final Runnable runnable = new Runnable() {
      @Override
      public void run() {
        anonymousToInner.invoke(project, new PsiElement[]{aClass}, dataContext);
      }
    };
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      runnable.run();
    }
    else {
      ApplicationManager.getApplication().invokeLater(runnable, project.getDisposed());
    }
  }
}
