package com.epam.jdi.uitests.web.selenium.elements.composite;
/*
 * Copyright 2004-2016 EPAM Systems
 *
 * This file is part of JDI project.
 *
 * JDI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JDI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JDI. If not, see <http://www.gnu.org/licenses/>.
 */


import com.epam.commons.LinqUtils;
import com.epam.commons.map.MapArray;
import com.epam.jdi.uitests.core.interfaces.base.IHasValue;
import com.epam.jdi.uitests.core.interfaces.base.ISetValue;
import com.epam.jdi.uitests.core.interfaces.common.IButton;
import com.epam.jdi.uitests.core.interfaces.complex.IForm;
import com.epam.jdi.uitests.core.utils.common.PrintUtils;
import com.epam.jdi.uitests.web.selenium.elements.base.Element;
import com.epam.jdi.uitests.web.selenium.elements.common.Button;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.epam.commons.LinqUtils.foreach;
import static com.epam.commons.PrintUtils.print;
import static com.epam.commons.ReflectionUtils.checkEntityIsNotNull;
import static com.epam.commons.ReflectionUtils.getFields;
import static com.epam.commons.ReflectionUtils.getValueField;
import static com.epam.commons.StringUtils.LINE_BREAK;
import static com.epam.commons.StringUtils.namesEqual;
import static com.epam.jdi.uitests.core.annotations.AnnotationsUtil.getElementName;
import static com.epam.jdi.uitests.core.settings.JDISettings.exception;
import static com.epam.jdi.uitests.core.utils.common.PrintUtils.objToSetValue;
import static java.lang.String.format;

/**
 * Created by Roman_Iovlev on 7/8/2015.
 */
public class Form<T> extends Element implements IForm<T> {
    protected Class<T> entityClass;
    public Form() {}
    public Form(Class<T> clazz) {
        this.entityClass = checkEntityIsNotNull(clazz);
    }

    protected void setValueAction(String text, ISetValue element) {
        element.setValue(text);
    }

    protected String getValueAction(IHasValue element) {
        return element.getValue();
    }

    /**
     * @param map Specify entity as map
     *            Fills all elements on the form which implements SetValue interface and can be matched with fields in input entity
     */
    public final void fill(MapArray<String, String> map) {
        foreach(getFields(this, ISetValue.class), element -> {
            String fieldValue = map.first((name, value) ->
                namesEqual(name, getElementName(element)));
            if (fieldValue == null)
                return;
            ISetValue setValueElement = (ISetValue) getValueField(element, this);
            doActionRule.accept(fieldValue, val -> setValueAction(val, setValueElement));
        });
    }

    private Button getSubmitButton() {
        List<Field> fields = getFields(this, IButton.class);
        switch (fields.size()) {
            case 0:
                throw exception("Can't find any buttons on form '%s.", toString());
            case 1:
                return (Button) getValueField(fields.get(0), this);
            default:
                throw exception("Form '%s' have more than 1 button. Use submit(entity, buttonName) for this case instead", toString());
        }
    }

    /**
     * @param objStrings Fill all SetValue elements and click on Button specified button e.g. "Publish" or "Save" <br>
     * @apiNote To use this option Form pageObject should have button names in specific format <br>
     * e.g. if you call "submit(user, "Publish") then you should have Element 'publishButton'. <br>
     * * Letters case in button name  no matters
     */
    public void submit(MapArray<String, String> objStrings) {
        fill(objStrings);
        getElementClass.getButton("submit").click();
    }

    private void setText(String text) {
        Field field = getFields(this, ISetValue.class).get(0);
        ISetValue setValueElement = (ISetValue) getValueField(field, this);
        doActionRule.accept(text, val -> setValueAction(val, setValueElement));
    }

    /**
     * @param text Specify text
     *             Fill first setable field with value and click on Button “submit” <br>
     * @apiNote To use this option Form pageObject should have at least one ISetValue element and only one IButton Element
     */
    public void submit(String text) {
        setText(text);
        getElementClass.getButton("submit").click();
    }

    /**
     * @param buttonName Specify Button Name
     * @param entity     Specify entity
     *                   Fill all SetValue elements and click on Button specified button e.g. "Publish" or "Save" <br>
     * @apiNote To use this option Form pageObject should have button names in specific format <br>
     * e.g. if you call "submit(user, "Publish") then you should have Element 'publishButton'. <br>
     * * Letters case in button name  no matters
     */
    public void submit(T entity, String buttonName) {
        fill(objToSetValue(entity));
        getElementClass.getButton(buttonName).click();
    }

    /**
     * @param text       Specify text
     * @param buttonName button name for form submiting
     *                   Fill first setable field with value and click on Button “buttonName” <br>
     * @apiNote To use this option Form pageObject should have at least one ISetValue element <br>
     * Allowed different buttons to send one form e.g. save/ publish / cancel / search update ...
     */
    public void submit(String text, String buttonName) {
        setText(text);
        getElementClass.getButton(buttonName).click();
    }

    /**
     * @param buttonName Specify Button Name
     * @param entity     Specify entity
     *                   Fill all SetValue elements and click on Button specified button e.g. "Publish" or "Save" <br>
     * @apiNote To use this option Form pageObject should have button names in specific format <br>
     * e.g. if you call "submit(user, "Publish") then you should have Element 'publishButton'. <br>
     * * Letters case in button name  no matters
     */
    public void submit(T entity, Enum buttonName) {
        fill(objToSetValue(entity));
        getElementClass.getButton(buttonName.toString().toLowerCase()).click();
    }

    public T getEntity() {
        return extractEntity(entityClass, this);
    }

    /**
     * @param objStrings Specify entity as mapArray
     *            Fills all elements on the form which implements SetValue interface and can be matched with fields in input entity
     */
    public List<String> verify(MapArray<String, String> objStrings) {
        List<String> compareFalse = new ArrayList<>();
        foreach(getFields(this, IHasValue.class), field -> {
            String fieldValue = objStrings.first((name, value) ->
                    namesEqual(name, getElementName(field)));
            if (fieldValue == null)
                return;
            IHasValue valueField = (IHasValue) getValueField(field, this);
            doActionRule.accept(fieldValue, expected -> {
                String actual = getValueAction(valueField).trim();
                if (actual.equals(expected))
                    return;
                compareFalse.add(format("Field '%s' (Actual: '%s' <> Expected: '%s')", field.getName(), actual, expected));
            });
        });
        return compareFalse;
    }

    /**
     * @param objStrings Specify entity as mapArray
     *            Verify that form filled correctly. If not throws error
     */
    public void check(MapArray<String, String> objStrings) {
        List<String> result = verify(objStrings);
        if (result.size() > 0)
            throw exception("Check form failed:" + LINE_BREAK + print(result, LINE_BREAK));
    }

    protected String getValueAction() {
        return print(LinqUtils.select(getFields(this, IHasValue.class), field ->
                ((IHasValue) getValueField(field, this)).getValue()));
    }

    protected void setValueAction(String value) {
        submit(PrintUtils.parseObjectAsString(value));
    }

    /**
     * @return Get value of Element
     */
    public final String getValue() {
        return actions.getValue(this::getValueAction);
    }

    /**
     * @param value Specify element value
     *              Set value to Element
     */
    public final void setValue(String value) {
        actions.setValue(value, this::setValueAction);
    }

}