/**
 * <copyright>
 *******************************************************************************
 * Copyright (c) 2004 Eteration Bilisim A.S.
 * All rights reserved. � This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL ETERATION A.S. OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Eteration Bilisim A.S.  For more
 * information on eteration, please see
 * <http://www.eteration.com/>.
 ***************************************************************************
 * </copyright>
 *
 * $Id: LaunchConfiguration.java,v 1.1 2004/11/20 21:18:10 ndai Exp $
 */
package org.eclipse.jst.server.generic.servertype.definition;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Launch Configuration</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getClass_ <em>Class</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getWorkingDirectory <em>Working Directory</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getProgramArguments <em>Program Arguments</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getVmParameters <em>Vm Parameters</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getClasspathReference <em>Classpath Reference</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getLaunchConfiguration()
 * @model 
 * @generated
 */
public interface LaunchConfiguration extends EObject {
	/**
	 * Returns the value of the '<em><b>Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Class</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Class</em>' attribute.
	 * @see #setClass(String)
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getLaunchConfiguration_Class()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 * @generated
	 */
	String getClass_();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getClass_ <em>Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Class</em>' attribute.
	 * @see #getClass_()
	 * @generated
	 */
	void setClass(String value);

	/**
	 * Returns the value of the '<em><b>Working Directory</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Working Directory</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Working Directory</em>' attribute.
	 * @see #setWorkingDirectory(String)
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getLaunchConfiguration_WorkingDirectory()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 * @generated
	 */
	String getWorkingDirectory();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getWorkingDirectory <em>Working Directory</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Working Directory</em>' attribute.
	 * @see #getWorkingDirectory()
	 * @generated
	 */
	void setWorkingDirectory(String value);

	/**
	 * Returns the value of the '<em><b>Program Arguments</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Program Arguments</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Program Arguments</em>' attribute.
	 * @see #setProgramArguments(String)
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getLaunchConfiguration_ProgramArguments()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 * @generated
	 */
	String getProgramArguments();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getProgramArguments <em>Program Arguments</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Program Arguments</em>' attribute.
	 * @see #getProgramArguments()
	 * @generated
	 */
	void setProgramArguments(String value);

	/**
	 * Returns the value of the '<em><b>Vm Parameters</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Vm Parameters</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Vm Parameters</em>' attribute.
	 * @see #setVmParameters(String)
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getLaunchConfiguration_VmParameters()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 * @generated
	 */
	String getVmParameters();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getVmParameters <em>Vm Parameters</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Vm Parameters</em>' attribute.
	 * @see #getVmParameters()
	 * @generated
	 */
	void setVmParameters(String value);

	/**
	 * Returns the value of the '<em><b>Classpath Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Classpath Reference</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Classpath Reference</em>' attribute.
	 * @see #setClasspathReference(String)
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getLaunchConfiguration_ClasspathReference()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 * @generated
	 */
	String getClasspathReference();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getClasspathReference <em>Classpath Reference</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Classpath Reference</em>' attribute.
	 * @see #getClasspathReference()
	 * @generated
	 */
	void setClasspathReference(String value);

} // LaunchConfiguration