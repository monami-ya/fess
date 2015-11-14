/*
 * Copyright 2012-2015 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.app.web.admin.webauth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.codelibs.fess.Constants;
import org.codelibs.fess.app.pager.WebAuthenticationPager;
import org.codelibs.fess.app.service.WebAuthenticationService;
import org.codelibs.fess.app.service.WebConfigService;
import org.codelibs.fess.app.web.CrudMode;
import org.codelibs.fess.app.web.base.FessAdminAction;
import org.codelibs.fess.es.config.exentity.WebAuthentication;
import org.codelibs.fess.es.config.exentity.WebConfig;
import org.codelibs.fess.helper.SystemHelper;
import org.codelibs.fess.util.ComponentUtil;
import org.dbflute.optional.OptionalEntity;
import org.dbflute.optional.OptionalThing;
import org.lastaflute.web.Execute;
import org.lastaflute.web.callback.ActionRuntime;
import org.lastaflute.web.response.HtmlResponse;
import org.lastaflute.web.response.next.HtmlNext;
import org.lastaflute.web.response.render.RenderData;
import org.lastaflute.web.util.LaRequestUtil;
import org.lastaflute.web.validation.VaErrorHook;

/**
 * @author shinsuke
 * @author Shunji Makino
 */
public class AdminWebauthAction extends FessAdminAction {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Resource
    private WebAuthenticationService webAuthenticationService;
    @Resource
    private WebAuthenticationPager webAuthenticationPager;
    @Resource
    private SystemHelper systemHelper;

    @Resource
    protected WebConfigService webConfigService;

    // ===================================================================================
    //                                                                               Hook
    //                                                                              ======
    @Override
    protected void setupHtmlData(final ActionRuntime runtime) {
        super.setupHtmlData(runtime);
        runtime.registerData("helpLink", systemHelper.getHelpLink(fessConfig.getOnlineHelpNameWebauth()));
    }

    // ===================================================================================
    //                                                                      Search Execute
    //                                                                      ==============
    @Execute
    public HtmlResponse index(final SearchForm form) {
        return asHtml(path_AdminWebauth_AdminWebauthJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse list(final OptionalThing<Integer> pageNumber, final SearchForm form) {
        pageNumber.ifPresent(num -> {
            webAuthenticationPager.setCurrentPageNumber(pageNumber.get());
        }).orElse(() -> {
            webAuthenticationPager.setCurrentPageNumber(0);
        });
        return asHtml(path_AdminWebauth_AdminWebauthJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse search(final SearchForm form) {
        copyBeanToBean(form, webAuthenticationPager, op -> op.exclude(Constants.PAGER_CONVERSION_RULE));
        return asHtml(path_AdminWebauth_AdminWebauthJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    @Execute
    public HtmlResponse reset(final SearchForm form) {
        webAuthenticationPager.clear();
        return asHtml(path_AdminWebauth_AdminWebauthJsp).renderWith(data -> {
            searchPaging(data, form);
        });
    }

    protected void searchPaging(final RenderData data, final SearchForm form) {
        data.register("webAuthenticationItems", webAuthenticationService.getWebAuthenticationList(webAuthenticationPager)); // page navi
        data.register("displayCreateLink", !webConfigService.getAllWebConfigList(false, false, false, null).isEmpty());
        // restore from pager
        copyBeanToBean(webAuthenticationPager, form, op -> op.include("id"));
    }

    // ===================================================================================
    //                                                                        Edit Execute
    //                                                                        ============
    // -----------------------------------------------------
    //                                            Entry Page
    //                                            ----------
    @Execute
    //(token = TxToken.SAVE)
    public HtmlResponse createnew() {
        return asHtml(path_AdminWebauth_AdminWebauthEditJsp).useForm(CreateForm.class, op -> {
            op.setup(form -> {
                form.initialize();
                form.crudMode = CrudMode.CREATE;
            });
        }).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerWebConfigItems(data);
        });
    }

    @Execute
    //(token = TxToken.SAVE)
    public HtmlResponse edit(final EditForm form) {
        validate(form, messages -> {}, toEditHtml());
        HtmlNext next;
        switch (form.crudMode) {
        case CrudMode.EDIT: // back
            form.crudMode = CrudMode.DETAILS;
            next = path_AdminWebauth_AdminWebauthDetailsJsp;
            break;
        default:
            form.crudMode = CrudMode.EDIT;
            next = path_AdminWebauth_AdminWebauthEditJsp;
            break;
        }
        final String id = form.id;
        webAuthenticationService.getWebAuthentication(id).ifPresent(entity -> {
            copyBeanToBean(entity, form, op -> {});
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), toEditHtml());
        });
        return asHtml(next).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerWebConfigItems(data);
        });
    }

    // -----------------------------------------------------
    //                                               Details
    //                                               -------
    @Execute
    public HtmlResponse details(final int crudMode, final String id) {
        verifyCrudMode(crudMode, CrudMode.DETAILS);
        return asHtml(path_AdminWebauth_AdminWebauthDetailsJsp).useForm(EditForm.class, op -> {
            op.setup(form -> {
                webAuthenticationService.getWebAuthentication(id).ifPresent(entity -> {
                    copyBeanToBean(entity, form, copyOp -> {
                        copyOp.excludeNull();
                    });
                    form.crudMode = crudMode;
                }).orElse(() -> {
                    throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), toEditHtml());
                });
            });
        }).renderWith(data -> {
            registerProtocolSchemeItems(data);
            registerWebConfigItems(data);
        });
    }

    // -----------------------------------------------------
    //                                         Actually Crud
    //                                         -------------
    @Execute
    public HtmlResponse create(final CreateForm form) {
        verifyCrudMode(form.crudMode, CrudMode.CREATE);
        validate(form, messages -> {}, toEditHtml());
        createWebAuthentication(form).ifPresent(entity -> {
            webAuthenticationService.store(entity);
            saveInfo(messages -> messages.addSuccessCrudCreateCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudFailedToCreateCrudTable(GLOBAL), toEditHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse update(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.EDIT);
        validate(form, messages -> {}, toEditHtml());
        createWebAuthentication(form).ifPresent(entity -> {
            webAuthenticationService.store(entity);
            saveInfo(messages -> messages.addSuccessCrudUpdateCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, form.id), toEditHtml());
        });
        return redirect(getClass());
    }

    @Execute
    public HtmlResponse delete(final EditForm form) {
        verifyCrudMode(form.crudMode, CrudMode.DETAILS);
        validate(form, messages -> {}, toEditHtml());
        final String id = form.id;
        webAuthenticationService.getWebAuthentication(id).ifPresent(entity -> {
            webAuthenticationService.delete(entity);
            saveInfo(messages -> messages.addSuccessCrudDeleteCrudTable(GLOBAL));
        }).orElse(() -> {
            throwValidationError(messages -> messages.addErrorsCrudCouldNotFindCrudTable(GLOBAL, id), toEditHtml());
        });
        return redirect(getClass());
    }

    //===================================================================================
    //                                                                        Assist Logic
    //                                                                        ============
    private OptionalEntity<WebAuthentication> getEntity(final CreateForm form, final String username, final long currentTime) {
        switch (form.crudMode) {
        case CrudMode.CREATE:
            if (form instanceof CreateForm) {
                return OptionalEntity.of(new WebAuthentication()).map(entity -> {
                    entity.setCreatedBy(username);
                    entity.setCreatedTime(currentTime);
                    return entity;
                });
            }
            break;
        case CrudMode.EDIT:
            if (form instanceof EditForm) {
                return webAuthenticationService.getWebAuthentication(((EditForm) form).id);
            }
            break;
        default:
            break;
        }
        return OptionalEntity.empty();
    }

    protected OptionalEntity<WebAuthentication> createWebAuthentication(final CreateForm form) {
        final String username = systemHelper.getUsername();
        final long currentTime = systemHelper.getCurrentTimeAsLong();
        return getEntity(form, username, currentTime).map(entity -> {
            entity.setUpdatedBy(username);
            entity.setUpdatedTime(currentTime);
            copyBeanToBean(form, entity, op -> op.exclude(Constants.COMMON_CONVERSION_RULE));
            return entity;
        });
    }

    protected void registerProtocolSchemeItems(final RenderData data) {
        final List<Map<String, String>> itemList = new ArrayList<Map<String, String>>();
        final Locale locale = LaRequestUtil.getRequest().getLocale();
        itemList.add(createItem(ComponentUtil.getMessageManager().getMessage(locale, "labels.web_authentication_scheme_basic"),
                Constants.BASIC));
        itemList.add(createItem(ComponentUtil.getMessageManager().getMessage(locale, "labels.web_authentication_scheme_digest"),
                Constants.DIGEST));
        itemList.add(createItem(ComponentUtil.getMessageManager().getMessage(locale, "labels.web_authentication_scheme_ntlm"),
                Constants.NTLM));
        data.register("protocolSchemeItems", itemList);
    }

    protected void registerWebConfigItems(final RenderData data) {
        final List<Map<String, String>> itemList = new ArrayList<Map<String, String>>();
        final List<WebConfig> webConfigList = webConfigService.getAllWebConfigList(false, false, false, null);
        for (final WebConfig webConfig : webConfigList) {
            itemList.add(createItem(webConfig.getName(), webConfig.getId().toString()));
        }
        data.register("webConfigItems", itemList);
    }

    protected Map<String, String> createItem(final String label, final String value) {
        final Map<String, String> map = new HashMap<String, String>(2);
        map.put(Constants.ITEM_LABEL, label);
        map.put(Constants.ITEM_VALUE, value);
        return map;
    }

    // ===================================================================================
    //                                                                        Small Helper
    //                                                                        ============
    protected void verifyCrudMode(final int crudMode, final int expectedMode) {
        if (crudMode != expectedMode) {
            throwValidationError(messages -> {
                messages.addErrorsCrudInvalidMode(GLOBAL, String.valueOf(expectedMode), String.valueOf(crudMode));
            }, toEditHtml());
        }
    }

    protected VaErrorHook toEditHtml() {
        return () -> {
            return asHtml(path_AdminWebauth_AdminWebauthEditJsp).renderWith(data -> {
                registerWebConfigItems(data);
            });
        };
    }
}