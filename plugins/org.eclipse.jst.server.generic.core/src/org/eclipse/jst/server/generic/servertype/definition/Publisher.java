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
 * $Id: Publisher.java,v 1.1 2004/11/20 21:18:10 ndai Exp $
 */
package org.eclipse.jst.server.generic.servertype.definition;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Publisher</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.Publisher#getGroup <em>Group</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.Publisher#getPublish <em>Publish</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.Publisher#getUnpublish <em>Unpublish</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.Publisher#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getPublisher()
 * @model 
 * @generated
 */
public interface Publisher extends EObject {
	/**
	 * Returns the value of the '<em><b>Group</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Group</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Group</em>' attribute list.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getPublisher_Group()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 * @generated
	 */
	FeatureMap getGroup();

	/**
	 * Returns the value of the '<em><b>Publish</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.jst.server.generic.servertype.definition.PublishType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Publish</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Publish</em>' containment reference list.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getPublisher_Publish()
	 * @model type="org.eclipse.jst.server.generic.servertype.definition.PublishType" containment="true" resolveProxies="false" required="true" transient="true" volatile="true" derived="true"
	 * @generated
	 */
	EList getPublish();

	/**
	 * Returns the value of the '<em><b>Unpublish</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.jst.server.generic.servertype.definition.UnpublishType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Unpublish</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Unpublish</em>' containment reference list.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getPublisher_Unpublish()
	 * @model type="org.eclipse.jst.server.generic.servertype.definition.UnpublishType" containment="true" resolveProxies="false" required="true" transient="true" volatile="true" derived="true"
	 * @generated
	 */
	EList getUnpublish();

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getPublisher_Id()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.Publisher#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

} // Publisher