/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.appium.espressoserver.lib.handlers;

import android.support.v4.view.ViewPager;

import androidx.test.espresso.EspressoException;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ScrollToAction;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.ViewPagerActions;
import io.appium.espressoserver.lib.handlers.exceptions.AppiumException;
import io.appium.espressoserver.lib.handlers.exceptions.InvalidArgumentException;
import io.appium.espressoserver.lib.helpers.AndroidLogger;
import io.appium.espressoserver.lib.model.Element;
import io.appium.espressoserver.lib.model.ScrollToPageParams;
import io.appium.espressoserver.lib.model.SetTimeParams;

public class ScrollToPage implements RequestHandler<ScrollToPageParams, Void> {

    @Override
    public Void handle(ScrollToPageParams params) throws AppiumException {
        ViewInteraction viewInteraction = Element.getViewInteractionById(params.getElementId());
        try {
            ViewAction scrollAction;
            Boolean smoothScroll = params.getSmoothScroll() == null ? false : params.getSmoothScroll();
            if (params.getScrollTo() != null) {
                switch (params.getScrollTo()) {
                    case FIRST:
                        scrollAction = ViewPagerActions.scrollToFirst(smoothScroll);
                        break;
                    case LAST:
                        scrollAction = ViewPagerActions.scrollToLast(smoothScroll);
                        break;
                    case LEFT:
                        scrollAction = ViewPagerActions.scrollLeft(smoothScroll);
                        break;
                    case RIGHT:
                        scrollAction = ViewPagerActions.scrollRight(smoothScroll);
                        break;
                    default:
                        throw new AppiumException(String.format("Invalid scrollTo param '%s'", params.getScrollTo()));
                }
            } else if (params.getScrollToPage() != null) {
                scrollAction = ViewPagerActions.scrollToPage(params.getScrollToPage(), smoothScroll);
            } else {
                throw new InvalidArgumentException("Could not complete scrollToPage action. Must provide either 'scrollTo' or 'scrollToPage'");
            }
            (new AndroidLogger()).info(String.format("Performing view pager action %s", scrollAction));
            viewInteraction.perform(scrollAction);

        } catch (ClassCastException e) {
            throw new AppiumException(String.format("Could not perform scroll to on element %s. Reason: %s", params.getElementId(), e.getCause()));
        } catch (Exception e) {
            if (e instanceof EspressoException) {
                throw new AppiumException(String.format("Could not scroll to page. Reason: %s", e.getCause()));
            }
            throw e;
        }
        return null;
    }
}
