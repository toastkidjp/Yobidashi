/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package jp.toastkid.article.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXRippler.RipplerMask;
import com.jfoenix.controls.JFXRippler.RipplerPos;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.svg.SVGGlyph;
import com.sun.javafx.scene.control.MultiplePropertyChangeListenerHandler;
import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;


/**
 * @author Shadi Shaheen
 * only support the default side of the tab pane (Side.Top)
 * @deprecated
 */
@Deprecated
public class MaterialTabPaneSkin extends BehaviorSkinBase<TabPane, TabPaneBehavior> {

    private final Color defaultColor = Color.valueOf("#00BCD4"), ripplerColor = Color.valueOf("FFFF8D"), selectedTabText = Color.WHITE;

    private Color tempLabelColor = Color.WHITE;

    private final HeaderContainer headerContainer;
    private final ObservableList<TabContentHolder> tabContentHolders;
    private final Rectangle tabPaneClip;
    private final Rectangle headerContainerClip;
    private Tab selectedTab;
    private boolean isSelectingTab = false;
    private double dragStart, offsetStart;
    private final AnchorPane tabsContainer;
    private final AnchorPane tabsContainerHolder;
    private static final int SPACER = 10;
    private double maxWidth = 0.0d;
    private double maxHeight = 0.0d;

    public MaterialTabPaneSkin(final TabPane tabPane) {
        super(tabPane, new TabPaneBehavior(tabPane));
        tabContentHolders = FXCollections.<TabContentHolder> observableArrayList();
        headerContainer = new HeaderContainer();
        getChildren().add(headerContainer);
        JFXDepthManager.setDepth(headerContainer, 1);

        tabsContainer = new AnchorPane();
        tabsContainerHolder = new AnchorPane();
        tabsContainerHolder.getChildren().add(tabsContainer);
        getChildren().add(tabsContainerHolder);

        // add tabs
        for (final Tab tab : getSkinnable().getTabs()) addTabContentHolder(tab);

        // clipping tabpane/header pane
        tabPaneClip = new Rectangle(tabPane.getWidth(), tabPane.getHeight());
        getSkinnable().setClip(tabPaneClip);
        headerContainerClip = new Rectangle();
        headerContainer.setClip(headerContainerClip);
        if (getSkinnable().getTabs().size() == 0) headerContainer.setVisible(false);

        // select a tab
        selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
        if (selectedTab == null && getSkinnable().getSelectionModel().getSelectedIndex() != -1) {
            getSkinnable().getSelectionModel().select(getSkinnable().getSelectionModel().getSelectedIndex());
            selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
        }
        // if no selected tab, then select the first tab
        if (selectedTab == null) getSkinnable().getSelectionModel().selectFirst();
        selectedTab = getSkinnable().getSelectionModel().getSelectedItem();

        headerContainer.headersRegion.setOnMouseDragged(me -> headerContainer.updateScrollOffset(offsetStart + me.getSceneX() - dragStart));
        getSkinnable().setOnMousePressed(me -> {
            dragStart = me.getSceneX();
            offsetStart = headerContainer.scrollOffset;
        });

        // add listeners on tab list
        getSkinnable().getTabs().addListener((ListChangeListener<Tab>) change -> {
            final List<Tab> tabsToBeRemoved = new ArrayList<>();
            final List<Tab> tabsToBeAdded = new ArrayList<>();
            int insertIndex = -1;
            while (change.next()) {
                if (change.wasPermutated()) {
                    final Tab selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
                    final List<Tab> permutatedTabs = new ArrayList<Tab>(change.getTo() - change.getFrom());
                    getSkinnable().getSelectionModel().clearSelection();
                    for (int i = change.getFrom(); i < change.getTo(); i++)
                        permutatedTabs.add(getSkinnable().getTabs().get(i));
                    removeTabs(permutatedTabs);
                    addTabs(permutatedTabs, change.getFrom());
                    getSkinnable().getSelectionModel().select(selectedTab);
                }
                if (change.wasRemoved())
                    tabsToBeRemoved.addAll(change.getRemoved());
                if (change.wasAdded()) {
                    tabsToBeAdded.addAll(change.getAddedSubList());
                    insertIndex = change.getFrom();
                }
            }
            // only remove the tabs that are not in tabsToBeAdded
            tabsToBeRemoved.removeAll(tabsToBeAdded);
            removeTabs(tabsToBeRemoved);
            // add the new tabs
            if (!tabsToBeAdded.isEmpty()) {
                for (final TabContentHolder tabContentHolder : tabContentHolders) {
                    final TabHeaderContainer tabHeaderContainer = headerContainer.getTabHeaderContainer(tabContentHolder.tab);
                    if (!tabHeaderContainer.isClosing && tabsToBeAdded.contains(tabContentHolder.tab))
                        tabsToBeAdded.remove(tabContentHolder.tab);
                }
                addTabs(tabsToBeAdded, insertIndex == -1 ? tabContentHolders.size() : insertIndex);
            }
            getSkinnable().requestLayout();
        });

        registerChangeListener(tabPane.getSelectionModel().selectedItemProperty(), "SELECTED_TAB");
        registerChangeListener(tabPane.widthProperty(), "WIDTH");
        registerChangeListener(tabPane.heightProperty(), "HEIGHT");

    }

    @Override
    protected void handleControlPropertyChanged(final String property) {
        super.handleControlPropertyChanged(property);
        if ("SELECTED_TAB".equals(property)) {
            isSelectingTab = true;
            selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
            getSkinnable().requestLayout();
        } else if ("WIDTH".equals(property)) {
            tabPaneClip.setWidth(getSkinnable().getWidth());
        } else if ("HEIGHT".equals(property)) {
            tabPaneClip.setHeight(getSkinnable().getHeight());
        }
    }

    private void removeTabs(final List<? extends Tab> removedTabs) {
        for (final Tab tab : removedTabs) {
            final TabHeaderContainer tabHeaderContainer = headerContainer.getTabHeaderContainer(tab);
            if (tabHeaderContainer != null) {
                tabHeaderContainer.isClosing = true;
                removeTab(tab);
                // if tabs list is empty hide the header container
                if (getSkinnable().getTabs().isEmpty()) headerContainer.setVisible(false);
            }
        }
    }

    private void addTabs(final List<? extends Tab> addedTabs, final int startIndex) {
        int i = 0;
        for (final Tab tab : addedTabs) {
            // show header container if we are adding the 1st tab
            if (!headerContainer.isVisible()) headerContainer.setVisible(true);
            headerContainer.addTab(tab, startIndex + i++, false);
            addTabContentHolder(tab);
            final TabHeaderContainer tabHeaderContainer = headerContainer.getTabHeaderContainer(tab);
            if (tabHeaderContainer != null) {
                tabHeaderContainer.setVisible(true);
                tabHeaderContainer.inner.requestLayout();
            }
        }
    }

    private void addTabContentHolder(final Tab tab) {
        // create new content place holder
        final TabContentHolder tabContentHolder = new TabContentHolder(tab);
        tabContentHolder.setClip(new Rectangle());
        tabContentHolders.add(tabContentHolder);
        // always add tab content holder below its header
        tabsContainer.getChildren().add(0, tabContentHolder);
    }

    private void removeTabContentHolder(final Tab tab) {
        for (final TabContentHolder tabContentHolder : tabContentHolders) {
            if (tabContentHolder.tab.equals(tab)) {
                tabContentHolder.removeListeners(tab);
                getChildren().remove(tabContentHolder);
                tabContentHolders.remove(tabContentHolder);
                tabsContainer.getChildren().remove(tabContentHolder);
                break;
            }
        }
    }

    private void removeTab(final Tab tab) {
        final TabHeaderContainer tabHeaderContainer = headerContainer.getTabHeaderContainer(tab);
        if (tabHeaderContainer != null) tabHeaderContainer.removeListeners(tab);
        headerContainer.removeTab(tab);
        removeTabContentHolder(tab);
        headerContainer.requestLayout();
    }

    @Override
    protected double computePrefWidth(final double height, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        for (final TabContentHolder tabContentHolder : tabContentHolders)
            maxWidth = Math.max(maxWidth, snapSize(tabContentHolder.prefWidth(-1)));
        final double headerContainerWidth = snapSize(headerContainer.prefWidth(-1));
        final double prefWidth = Math.max(maxWidth, headerContainerWidth);
        return snapSize(prefWidth) + rightInset + leftInset;
    }
    @Override
    protected double computePrefHeight(final double width, final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        for (final TabContentHolder tabContentHolder : tabContentHolders)
            maxHeight = Math.max(maxHeight, snapSize(tabContentHolder.prefHeight(-1)));
        final double headerContainerHeight = snapSize(headerContainer.prefHeight(-1));
        final double prefHeight = maxHeight + snapSize(headerContainerHeight);
        return snapSize(prefHeight) + topInset + bottomInset;
    }
    @Override
    public double computeBaselineOffset(final double topInset, final double rightInset, final double bottomInset, final double leftInset) {
        return headerContainer.getBaselineOffset() + topInset;
    }

    /*
     *  keep track of indecies after changing the tabs, it used to fix
     *  tabs animation after changing the tabs (remove/add)
     */
    private int diffTabsIndices = 0;


    @Override
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        final double headerHeight = snapSize(headerContainer.prefHeight(-1));
        headerContainer.resize(w, headerHeight);
        headerContainer.relocate(x, y);
        headerContainerClip.setX(0);
        headerContainerClip.setY(0);
        headerContainerClip.setWidth(w);
        headerContainerClip.setHeight(headerHeight + 10); // 10 is the height of the shadow effect

        // position the tab content for the selected tab only
        double contentStartX = 0;
        double contentStartY = 0;
        contentStartX = x;
        contentStartY = y + headerHeight;
        final double contentWidth = w - (0);
        final double contentHeight = h - (headerHeight);

        final Rectangle clip = new Rectangle(contentWidth,contentHeight);
        tabsContainerHolder.setClip(clip);
        tabsContainerHolder.resize(contentWidth, contentHeight);
        tabsContainerHolder.relocate(contentStartX, contentStartY);
        tabsContainer.resize(contentWidth * tabContentHolders.size(), contentHeight);

        for (int i = 0, max = tabContentHolders.size(); i < max; i++) {
            final TabContentHolder tabContentHolder = tabContentHolders.get(i);
            tabContentHolder.setVisible(true);
            tabContentHolder.setTranslateX(contentWidth*i);
            if (tabContentHolder.getClip() != null) {
                ((Rectangle) tabContentHolder.getClip()).setWidth(contentWidth);
                ((Rectangle) tabContentHolder.getClip()).setHeight(contentHeight);
            }
            if(tabContentHolder.tab == selectedTab){
                final int index = getSkinnable().getTabs().indexOf(selectedTab);
                if(index != i) {
                    tabsContainer.setTranslateX(-contentWidth*i);
                    diffTabsIndices = i - index;
                }else{
                    // fix X translation after changing the tabs
                    if(diffTabsIndices!=0){
                        tabsContainer.setTranslateX(tabsContainer.getTranslateX() + contentWidth*diffTabsIndices);
                        diffTabsIndices = 0;
                    }
                    // animate upon tab selection only otherwise just translate the selected tab
                    if(isSelectingTab) new Timeline(new KeyFrame(Duration.millis(320), new KeyValue(tabsContainer.translateXProperty(), -contentWidth*index, Interpolator.EASE_BOTH))).play();
                    else tabsContainer.setTranslateX(-contentWidth*index);
                }
            }
            tabContentHolder.resize(contentWidth, contentHeight);
        }
    }

    /**************************************************************************
     *                                                                        *
     * HeaderContainer: tabs headers container                                *
     *                                                                        *
     **************************************************************************/
    protected class HeaderContainer extends StackPane {

        private Rectangle headerClip;
        private StackPane headersRegion;
        private StackPane headerBackground;
        private HeaderControl rightControlButton;
        private HeaderControl leftControlButton;
        private Line selectedTabLine;
        private boolean initialized = false;
        private boolean measureClosingTabs = false;
        private double scrollOffset, selectedTabLineOffset;

        public HeaderContainer() {
            getStyleClass().setAll("tab-header-area");
            setManaged(false);
            headerClip = new Rectangle();
            headersRegion = new StackPane() {
                @Override
                protected double computePrefWidth(final double height) {
                    double width = 0.0F;
                    for (final Node child : getChildren())
                        if(child instanceof TabHeaderContainer)
                            if (child.isVisible() && (measureClosingTabs || !((TabHeaderContainer) child).isClosing))
                                width += ((TabHeaderContainer) child).prefWidth(height);
                    return snapSize(width) + snappedLeftInset() + snappedRightInset();
                }

                @Override
                protected double computePrefHeight(final double width) {
                    double height = 0.0F;
                    for (final Node child : getChildren())
                        if(child instanceof TabHeaderContainer)
                            height = Math.max(height, ((TabHeaderContainer) child).prefHeight(width));
                    return snapSize(height) + snappedTopInset() + snappedBottomInset();
                }

                @Override
                protected void layoutChildren() {
                    if (isTabsFitHeaderWidth()) {
                        updateScrollOffset(0.0);
                    } else {
                        if (!removedTabsHeaders.isEmpty()) {
                            double offset = 0;
                            final double w = headerContainer.getWidth() - snapSize(rightControlButton.prefWidth(-1)) - snapSize(leftControlButton.prefWidth(-1)) - snappedLeftInset() - SPACER;
                            final Iterator<Node> itr = getChildren().iterator();
                            while (itr.hasNext()) {
                                final Node temp = itr.next();
                                if(temp instanceof TabHeaderContainer){
                                    final TabHeaderContainer tabHeaderContainer = (TabHeaderContainer) temp;
                                    final double containerPrefWidth = snapSize(tabHeaderContainer.prefWidth(-1));
                                    // if tab has been removed
                                    if (removedTabsHeaders.contains(tabHeaderContainer)) {
                                        if (offset < w) isSelectingTab = true;
                                        itr.remove();
                                        removedTabsHeaders.remove(tabHeaderContainer);
                                        if (removedTabsHeaders.isEmpty()) break;
                                    }
                                    offset += containerPrefWidth;
                                }
                            }
                        }
                    }

                    if (isSelectingTab) {
                        // make sure the selected tab is visible
                        double offset = 0.0;
                        double selectedTabOffset = 0.0;
                        double selectedTabWidth = 0.0;
                        for (final Node node : headersRegion.getChildren()) {
                            if(node instanceof TabHeaderContainer){
                                final TabHeaderContainer tabHeader = (TabHeaderContainer) node;
                                final double tabHeaderPrefWidth = snapSize(tabHeader.prefWidth(-1));
                                if (selectedTab != null && selectedTab.equals(tabHeader.tab)) {
                                    selectedTabOffset = offset;
                                    selectedTabWidth = tabHeaderPrefWidth;
                                }
                                offset += tabHeaderPrefWidth;
                            }
                        }
                        // animate the tab selection
                        runTimeline(selectedTabOffset + 1, selectedTabWidth - 2);
                        isSelectingTab = false;
                    } else {
                        // validate scroll offset
                        updateScrollOffset(scrollOffset);
                    }

                    final double tabBackgroundHeight = snapSize(prefHeight(-1));
                    double tabStartX = scrollOffset;
                    updateHeaderContainerClip();
                    for (final Node node : getChildren()) {
                        if(node instanceof TabHeaderContainer){
                            final TabHeaderContainer tabHeaderContainer = (TabHeaderContainer) node;
                            final double tabHeaderPrefWidth = snapSize(tabHeaderContainer.prefWidth(-1));
                            final double tabHeaderPrefHeight = snapSize(tabHeaderContainer.prefHeight(-1));
                            tabHeaderContainer.resize(tabHeaderPrefWidth, tabHeaderPrefHeight);
                            final double tabStartY = tabBackgroundHeight - tabHeaderPrefHeight - snappedBottomInset();
                            tabHeaderContainer.relocate(tabStartX, tabStartY);
                            tabStartX += tabHeaderPrefWidth;
                        }
                    }
                }
            };

            headersRegion.getStyleClass().setAll("headers-region");
            headersRegion.setCache(true);
            headersRegion.setClip(headerClip);

            headerBackground = new StackPane();
            headerBackground.setBackground(new Background(new BackgroundFill(defaultColor, CornerRadii.EMPTY, Insets.EMPTY)));
            headerBackground.getStyleClass().setAll("tab-header-background");

            selectedTabLine = new Line();
            selectedTabLine.setCache(true);
            selectedTabLine.getStyleClass().add("tab-selected-line");
            selectedTabLine.setStrokeWidth(2);
            selectedTabLine.setStroke(ripplerColor);
            headersRegion.getChildren().add(selectedTabLine);
            selectedTabLine.translateYProperty().bind(Bindings.createDoubleBinding(()-> headersRegion.getHeight() - selectedTabLine.getStrokeWidth() + 1, headersRegion.heightProperty(), selectedTabLine.strokeWidthProperty()));

            rightControlButton = new HeaderControl(ArrowPosition.RIGHT);
            leftControlButton = new HeaderControl(ArrowPosition.LEFT);
            rightControlButton.setVisible(false);
            leftControlButton.setVisible(false);
            rightControlButton.inner.prefHeightProperty().bind(headersRegion.heightProperty());
            leftControlButton.inner.prefHeightProperty().bind(headersRegion.heightProperty());

            getChildren().addAll(headerBackground, headersRegion, leftControlButton, rightControlButton);

            int i = 0;
            for (final Tab tab : getSkinnable().getTabs())
                addTab(tab, i++, true);

            // support for mouse scroll of header area
            addEventHandler(ScrollEvent.SCROLL, (final ScrollEvent e) ->  updateScrollOffset(scrollOffset - e.getDeltaY()));
        }

        private void updateHeaderContainerClip() {
            final double x = 0, y = 0;
            double clipWidth = 0, clipHeight = 0;
            final double clipOffset = snappedLeftInset();
            double maxWidth = 0;
            double controlPrefWidth = 2 * snapSize(rightControlButton.prefWidth(-1));

            measureClosingTabs = true;
            final double headersPrefWidth = snapSize(headersRegion.prefWidth(-1));
            measureClosingTabs = false;

            final double headersPrefHeight = snapSize(headersRegion.prefHeight(-1));

            // Add the spacer if the control buttons are shown
            if (controlPrefWidth > 0)  controlPrefWidth = controlPrefWidth + SPACER;

            maxWidth = snapSize(getWidth()) - controlPrefWidth - clipOffset;
            clipWidth = (headersPrefWidth < maxWidth ? headersPrefWidth : maxWidth);
            clipHeight = headersPrefHeight;

            headerClip.setX(x);
            headerClip.setY(y);
            headerClip.setWidth(clipWidth + 10);
            headerClip.setHeight(clipHeight);
        }

        private void addTab(final Tab tab, final int addToIndex, final boolean visible) {
            final TabHeaderContainer tabHeaderSkin = new TabHeaderContainer(tab);
            tabHeaderSkin.setVisible(visible);
            headersRegion.getChildren().add(addToIndex, tabHeaderSkin);
        }

        private final List<TabHeaderContainer> removedTabsHeaders = new ArrayList<>();

        private void removeTab(final Tab tab) {
            final TabHeaderContainer tabHeaderContainer = getTabHeaderContainer(tab);
            if (tabHeaderContainer != null) {
                if (isTabsFitHeaderWidth()) {
                    headersRegion.getChildren().remove(tabHeaderContainer);
                } else {
                    // we need to keep track of the removed tab headers
                    // to compute scroll offset of the header
                    removedTabsHeaders.add(tabHeaderContainer);
                    tabHeaderContainer.removeListeners(tab);
                }
            }
        }

        private TabHeaderContainer getTabHeaderContainer(final Tab tab) {
            for (final Node child : headersRegion.getChildren())
                if(child instanceof TabHeaderContainer)
                    if (((TabHeaderContainer) child).tab.equals(tab))
                        return (TabHeaderContainer) child;
            return null;
        }

        private boolean isTabsFitHeaderWidth() {
            final double headerPrefWidth = snapSize(headersRegion.prefWidth(-1));
            final double rightControlWidth = 2 * snapSize(rightControlButton.prefWidth(-1));
            final double visibleWidth = headerPrefWidth + rightControlWidth + snappedLeftInset() + SPACER;
            return visibleWidth < getWidth();
        }

        private void runTimeline(final double newTransX, final double newWidth) {
            final double oldWidth = selectedTabLine.getEndX();
            final double oldTransX = selectedTabLineOffset;

            selectedTabLineOffset = newTransX;
            final double transDiff = newTransX - oldTransX;
            Timeline timeline;
            if(transDiff > 0) {
                timeline = new Timeline(
                        new KeyFrame(
                                Duration.ZERO,
                                new KeyValue(selectedTabLine.endXProperty(), oldWidth, Interpolator.EASE_BOTH),
                                new KeyValue(selectedTabLine.translateXProperty(), oldTransX + offsetStart, Interpolator.EASE_BOTH)),
                        new KeyFrame(
                                Duration.seconds(0.15),
                                new KeyValue(selectedTabLine.endXProperty(), transDiff, Interpolator.EASE_BOTH),
                                new KeyValue(selectedTabLine.translateXProperty(), oldTransX + offsetStart, Interpolator.EASE_BOTH)),
                        new KeyFrame(
                                Duration.seconds(0.33),
                                new KeyValue(selectedTabLine.endXProperty(), newWidth, Interpolator.EASE_BOTH),
                                new KeyValue(selectedTabLine.translateXProperty(), newTransX + offsetStart, Interpolator.EASE_BOTH))
                        );
            } else {
                timeline = new Timeline(
                        new KeyFrame(
                                Duration.ZERO,
                                new KeyValue(selectedTabLine.endXProperty(), oldWidth, Interpolator.EASE_BOTH),
                                new KeyValue(selectedTabLine.translateXProperty(), oldTransX + offsetStart, Interpolator.EASE_BOTH)),
                        new KeyFrame(
                                Duration.seconds(0.15),
                                new KeyValue(selectedTabLine.endXProperty(), -transDiff, Interpolator.EASE_BOTH),
                                new KeyValue(selectedTabLine.translateXProperty(), newTransX + offsetStart, Interpolator.EASE_BOTH)),
                        new KeyFrame(
                                Duration.seconds(0.33),
                                new KeyValue(selectedTabLine.endXProperty(), newWidth, Interpolator.EASE_BOTH),
                                new KeyValue(selectedTabLine.translateXProperty(), newTransX + offsetStart, Interpolator.EASE_BOTH))
                        );
            }
            timeline.play();
        }

        public void updateScrollOffset(final double newOffset) {
            final double tabPaneWidth = snapSize(getSkinnable().getWidth());
            final double controlTabWidth = 2 * snapSize(rightControlButton.getWidth());
            final double visibleWidth = tabPaneWidth - controlTabWidth - snappedLeftInset() - SPACER;

            // compute all tabs headers width
            double offset = 0.0;
            for (final Node node : headersRegion.getChildren())
                if(node instanceof TabHeaderContainer){
                    final double tabHeaderPrefWidth = snapSize(((TabHeaderContainer) node).prefWidth(-1));
                    offset += tabHeaderPrefWidth;
                }

            double actualOffset = newOffset;
            if ((visibleWidth - newOffset) > offset && newOffset < 0)  actualOffset = visibleWidth - offset;
            else if (newOffset > 0) actualOffset = 0;

            if (actualOffset != scrollOffset) {
                scrollOffset = actualOffset;
                headersRegion.requestLayout();
                selectedTabLine.setTranslateX(selectedTabLineOffset + scrollOffset);
            }
        }

        @Override
        protected double computePrefWidth(final double height) {
            return snapSize(headersRegion.prefWidth(height)) + 2 * rightControlButton.prefWidth(height) + snappedLeftInset() + SPACER + snappedLeftInset() + snappedRightInset();
        }

        @Override
        protected double computePrefHeight(final double width) {
            return snapSize(headersRegion.prefHeight(-1)) + snappedTopInset() + snappedBottomInset();
        }

        @Override
        public double getBaselineOffset() {
            return headersRegion.getBaselineOffset() + snappedTopInset();
        }

        @Override
        protected void layoutChildren() {
            final double leftInset = snappedLeftInset();
            final double rightInset = snappedRightInset();
            final double topInset = snappedTopInset();
            final double bottomInset = snappedBottomInset();
            final double w = snapSize(getWidth()) - (leftInset + rightInset);
            final double h = snapSize(getHeight()) - (topInset + bottomInset);
            final double tabBackgroundHeight = snapSize(prefHeight(-1));
            final double headersPrefWidth = snapSize(headersRegion.prefWidth(-1));
            final double headersPrefHeight = snapSize(headersRegion.prefHeight(-1));

            rightControlButton.showTabsMenu(!isTabsFitHeaderWidth());
            leftControlButton.showTabsMenu(!isTabsFitHeaderWidth());

            updateHeaderContainerClip();
            headersRegion.requestLayout();

            // layout left/right controls buttons
            final double btnWidth = snapSize(rightControlButton.prefWidth(-1));
            final double btnHeight = rightControlButton.prefHeight(btnWidth);
            rightControlButton.resize(btnWidth, btnHeight);
            leftControlButton.resize(btnWidth, btnHeight);

            // layout tabs
            headersRegion.resize(headersPrefWidth, headersPrefHeight);
            headerBackground.resize(snapSize(getWidth()), snapSize(getHeight()));

            double startX = 0;
            double startY = 0;
            double controlStartX = 0;
            double controlStartY = 0;

            startX = leftInset;
            startY = tabBackgroundHeight - headersPrefHeight - bottomInset;
            controlStartX = w - btnWidth + leftInset;
            controlStartY = snapSize(getHeight()) - btnHeight - bottomInset;

            if (headerBackground.isVisible()) positionInArea(headerBackground, 0, 0, snapSize(getWidth()), snapSize(getHeight()), 0, HPos.CENTER, VPos.CENTER);
            positionInArea(headersRegion, startX + btnWidth, startY, w, h, 0, HPos.LEFT, VPos.CENTER);
            positionInArea(rightControlButton, controlStartX, controlStartY, btnWidth, btnHeight, 0, HPos.CENTER, VPos.CENTER);
            positionInArea(leftControlButton, 0, controlStartY, btnWidth, btnHeight, 0, HPos.CENTER, VPos.CENTER);

            if (!initialized) {
                double offset = 0.0;
                double selectedTabOffset = 0.0;
                double selectedTabWidth = 0.0;
                for (final Node node : headersRegion.getChildren()) {
                    if(node instanceof TabHeaderContainer){
                        final TabHeaderContainer tabHeader = (TabHeaderContainer) node;
                        final double tabHeaderPrefWidth = snapSize(tabHeader.prefWidth(-1));
                        if (selectedTab != null && selectedTab.equals(tabHeader.tab)) {
                            selectedTabOffset = offset;
                            selectedTabWidth = tabHeaderPrefWidth;
                        }
                        offset += tabHeaderPrefWidth;
                    }
                }
                final double selectedTabStartX = selectedTabOffset;
                runTimeline(selectedTabStartX + 1, selectedTabWidth-2);
                initialized = true;
            }
        }
    }


    /**************************************************************************
     *                                                                        *
     * TabHeaderContainer: each tab Container                                 *
     *                                                                        *
     **************************************************************************/

    protected class TabHeaderContainer extends StackPane {
        private Tab tab = null;
        private Label tabText;
        private Tooltip oldTooltip;
        private Tooltip tooltip;
        private BorderPane inner;
        private JFXRippler rippler;
        private boolean systemChange = false;
        private boolean isClosing = false;

        private final MultiplePropertyChangeListenerHandler listener = new MultiplePropertyChangeListenerHandler(param -> {
            handlePropertyChanged(param);
            return null;
        });

        private final ListChangeListener<String> styleClassListener = (final Change<? extends String> change)-> getStyleClass().setAll(tab.getStyleClass());
        private final WeakListChangeListener<String> weakStyleClassListener = new WeakListChangeListener<>(styleClassListener);
        public TabHeaderContainer(final Tab tab) {
            this.tab = tab;
            getStyleClass().setAll(tab.getStyleClass());
            setId(tab.getId());
            setStyle(tab.getStyle());

            tabText = new Label(tab.getText(), tab.getGraphic());
            tabText.setFont(Font.font("", FontWeight.BOLD, 16));
            tabText.setPadding(new Insets(5, 10, 5, 10));
            tabText.getStyleClass().setAll("tab-label");

            inner = new BorderPane();
            inner.setCenter(tabText);
            inner.getStyleClass().add("tab-container");

            rippler = new JFXRippler(inner, RipplerPos.FRONT);
            rippler.setRipplerFill(ripplerColor);
            getChildren().addAll(rippler);

            tooltip = tab.getTooltip();
            if (tooltip != null) {
                Tooltip.install(this, tooltip);
                oldTooltip = tooltip;
            }

            if (tab.isSelected()) tabText.setTextFill(selectedTabText);
            else tabText.setTextFill(tempLabelColor.deriveColor(0, 0, 0.9, 1));


            tabText.textFillProperty().addListener((o,oldVal,newVal)->{
                if(!systemChange) tempLabelColor = (Color) newVal;
            });

            tab.selectedProperty().addListener((o, oldVal, newVal) -> {
                systemChange = true;
                if (newVal)  tabText.setTextFill(tempLabelColor);
                else tabText.setTextFill(tempLabelColor.deriveColor(0, 0, 0.9, 1));
                systemChange = false;
            });


            listener.registerChangeListener(tab.selectedProperty(), "SELECTED");
            listener.registerChangeListener(tab.textProperty(), "TEXT");
            listener.registerChangeListener(tab.graphicProperty(), "GRAPHIC");
            listener.registerChangeListener(tab.tooltipProperty(), "TOOLTIP");
            listener.registerChangeListener(tab.disableProperty(), "DISABLE");
            listener.registerChangeListener(tab.styleProperty(), "STYLE");
            listener.registerChangeListener(getSkinnable().tabMinWidthProperty(), "TAB_MIN_WIDTH");
            listener.registerChangeListener(getSkinnable().tabMaxWidthProperty(), "TAB_MAX_WIDTH");
            listener.registerChangeListener(getSkinnable().tabMinHeightProperty(), "TAB_MIN_HEIGHT");
            listener.registerChangeListener(getSkinnable().tabMaxHeightProperty(), "TAB_MAX_HEIGHT");
            tab.getStyleClass().addListener(weakStyleClassListener);

            getProperties().put(Tab.class, tab);

            setOnMouseClicked((event)->{
                if (tab.isDisable()) return;
                setOpacity(1);
                getBehavior().selectTab(tab);
                if (event.getPickResult().getIntersectedPoint().getX() < 42) {
                    removeTab(tab);
                }
            });

            if (tab.isClosable()) {
                final Button closeButton = new JFXButton("x");
                closeButton.setOnAction(e -> {});
                tab.setGraphic(closeButton);
            }

            // initialize pseudo-class state
            pseudoClassStateChanged(SELECTED_PSEUDOCLASS_STATE, tab.isSelected());
            pseudoClassStateChanged(DISABLED_PSEUDOCLASS_STATE, tab.isDisable());
        }

        private void handlePropertyChanged(final String p) {
            if ("SELECTED".equals(p)) {
                pseudoClassStateChanged(SELECTED_PSEUDOCLASS_STATE, tab.isSelected());
                inner.requestLayout();
                requestLayout();
            } else if ("TEXT".equals(p)) {
                tabText.setText(tab.getText());
            } else if ("GRAPHIC".equals(p)) {
                tabText.setGraphic(tab.getGraphic());
            } else if ("TOOLTIP".equals(p)) {
                // install new Toolip/ uninstall the old one
                if (oldTooltip != null) Tooltip.uninstall(this, oldTooltip);
                tooltip = tab.getTooltip();
                if (tooltip != null) {
                    Tooltip.install(this, tooltip);
                    oldTooltip = tooltip;
                }
            } else if ("DISABLE".equals(p)) {
                pseudoClassStateChanged(DISABLED_PSEUDOCLASS_STATE, tab.isDisable());
                inner.requestLayout();
                requestLayout();
            } else if ("STYLE".equals(p)) {
                setStyle(tab.getStyle());
            } else if ("TAB_MIN_WIDTH".equals(p)) {
                requestLayout();
                getSkinnable().requestLayout();
            } else if ("TAB_MAX_WIDTH".equals(p)) {
                requestLayout();
                getSkinnable().requestLayout();
            } else if ("TAB_MIN_HEIGHT".equals(p)) {
                requestLayout();
                getSkinnable().requestLayout();
            } else if ("TAB_MAX_HEIGHT".equals(p)) {
                requestLayout();
                getSkinnable().requestLayout();
            }
        }

        private void removeListeners(final Tab tab) {
            listener.dispose();
            inner.getChildren().clear();
            getChildren().clear();
        }

        @Override
        protected double computePrefWidth(final double height) {
            final double minWidth = snapSize(getSkinnable().getTabMinWidth());
            final double maxWidth = snapSize(getSkinnable().getTabMaxWidth());
            final double paddingRight = snappedRightInset();
            final double paddingLeft = snappedLeftInset();
            double tmpPrefWidth = snapSize(tabText.prefWidth(-1));

            if (tmpPrefWidth > maxWidth) {
                tmpPrefWidth = maxWidth;
            } else if (tmpPrefWidth < minWidth) {
                tmpPrefWidth = minWidth;
            }
            tmpPrefWidth += paddingRight + paddingLeft;
            return tmpPrefWidth;
        }

        @Override
        protected double computePrefHeight(final double width) {
            final double minHeight = snapSize(getSkinnable().getTabMinHeight());
            final double maxHeight = snapSize(getSkinnable().getTabMaxHeight());
            final double paddingTop = snappedTopInset();
            final double paddingBottom = snappedBottomInset();
            double tmpPrefHeight = snapSize(tabText.prefHeight(width));

            if (tmpPrefHeight > maxHeight) {
                tmpPrefHeight = maxHeight;
            } else if (tmpPrefHeight < minHeight) {
                tmpPrefHeight = minHeight;
            }
            tmpPrefHeight += paddingTop + paddingBottom;
            return tmpPrefHeight;
        }

        @Override
        protected void layoutChildren() {
            final double w = (snapSize(getWidth()) - snappedRightInset() - snappedLeftInset());
            rippler.resize(w, snapSize(getHeight()) - snappedTopInset() - snappedBottomInset());
            rippler.relocate(snappedLeftInset(), snappedTopInset());
        }

        @Override
        protected void setWidth(final double value) {
            super.setWidth(value);
        }

        @Override
        protected void setHeight(final double value) {
            super.setHeight(value);
        }
    }

    private static final PseudoClass SELECTED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("selected");
    private static final PseudoClass DISABLED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("disabled");


    /**************************************************************************
     *                                                                        *
     * TabContentHolder: each tab content container                           *
     *                                                                        *
     **************************************************************************/
    protected class TabContentHolder extends StackPane {
        private final Tab tab;
        private final InvalidationListener tabContentListener;
        private final InvalidationListener tabSelectedListener;
        private final WeakInvalidationListener weakTabContentListener;
        private final WeakInvalidationListener weakTabSelectedListener;

        public TabContentHolder(final Tab tab) {
            this.tab = tab;

            tabContentListener = valueModel -> updateContent();
            tabSelectedListener = valueModel -> setVisible(tab.isSelected());
            weakTabContentListener = new WeakInvalidationListener(tabContentListener);
            weakTabSelectedListener = new WeakInvalidationListener(tabSelectedListener);

            getStyleClass().setAll("tab-content-area");
            setManaged(false);
            updateContent();
            setVisible(tab.isSelected());
            tab.selectedProperty().addListener(weakTabSelectedListener);
            tab.contentProperty().addListener(weakTabContentListener);
        }

        private void updateContent() {
            final Node newContent = tab.getContent();
            if (newContent == null) getChildren().clear();
            else getChildren().setAll(newContent);
        }

        private void removeListeners(final Tab tab) {
            tab.selectedProperty().removeListener(weakTabSelectedListener);
            tab.contentProperty().removeListener(weakTabContentListener);
        }
    }

    private enum ArrowPosition {
        RIGHT, LEFT;
    }

    /**************************************************************************
     *                                                                        *
     * HeaderControl: left/right controls to interact with HeaderContainer*
     *                                                                        *
     **************************************************************************/
    protected class HeaderControl extends StackPane {
        private StackPane inner;
        private boolean showControlButtons, isLeftArrow;
        private Timeline arrowAnimation;
        private SVGGlyph arrowButton;
        private final SVGGlyph leftChevron = new SVGGlyph(0, "CHEVRON_LEFT", "M 742,-37 90,614 Q 53,651 53,704.5 53,758 90,795 l 652,651 q 37,37 90.5,37 53.5,0 90.5,-37 l 75,-75 q 37,-37 37,-90.5 0,-53.5 -37,-90.5 L 512,704 998,219 q 37,-38 37,-91 0,-53 -37,-90 L 923,-37 Q 886,-74 832.5,-74 779,-74 742,-37 z", Color.WHITE);
        private final SVGGlyph rightChevron = new SVGGlyph(0, "CHEVRON_RIGHT", "m 1099,704 q 0,-52 -37,-91 L 410,-38 q -37,-37 -90,-37 -53,0 -90,37 l -76,75 q -37,39 -37,91 0,53 37,90 l 486,486 -486,485 q -37,39 -37,91 0,53 37,90 l 76,75 q 36,38 90,38 54,0 90,-38 l 652,-651 q 37,-37 37,-90 z", Color.WHITE);

        public HeaderControl(final ArrowPosition pos) {
            getStyleClass().setAll("control-buttons-tab");
            isLeftArrow = pos == ArrowPosition.LEFT;
            arrowButton = isLeftArrow ? leftChevron : rightChevron;
            arrowButton.setStyle("-fx-min-width:0.8em;-fx-max-width:0.8em;-fx-min-height:1.3em;-fx-max-height:1.3em;");
            arrowButton.getStyleClass().setAll("tab-down-button");
            arrowButton.setVisible(isControlButtonShown());
            arrowButton.setFill(selectedTabText);

            final DoubleProperty offsetProperty = new SimpleDoubleProperty(0);
            offsetProperty.addListener((o,oldVal,newVal)-> headerContainer.updateScrollOffset(newVal.doubleValue()));

            final StackPane container = new StackPane(arrowButton);
            container.getStyleClass().add("container");
            container.setPadding(new Insets(7));
            container.setCursor(Cursor.HAND);

            container.setOnMousePressed(press ->{
                offsetProperty.set(headerContainer.scrollOffset);
                final double offset = isLeftArrow ? headerContainer.scrollOffset + headerContainer.headersRegion.getWidth() : headerContainer.scrollOffset - headerContainer.headersRegion.getWidth();
                arrowAnimation = new Timeline(new KeyFrame(Duration.seconds(1), new KeyValue(offsetProperty, offset , Interpolator.LINEAR)));
                arrowAnimation.play();
            });
            container.setOnMouseReleased(release -> arrowAnimation.stop());
            final JFXRippler arrowRippler = new JFXRippler(container,RipplerMask.CIRCLE,RipplerPos.BACK);
            arrowRippler.ripplerFillProperty().bind(arrowButton.fillProperty());
            StackPane.setMargin(arrowButton, new Insets(0,0,0,isLeftArrow?-4:4));

            inner = new StackPane() {
                @Override
                protected double computePrefWidth(final double height) {
                    double preferWidth = 0.0d;
                    final double maxArrowWidth = !isControlButtonShown() ? 0 : snapSize(arrowRippler.prefWidth(getHeight()));
                    preferWidth += isControlButtonShown()? maxArrowWidth : 0;
                    preferWidth += (preferWidth > 0)? snappedLeftInset() + snappedRightInset() : 0;
                    return preferWidth;
                }
                @Override
                protected double computePrefHeight(final double width) {
                    double prefHeight = 0.0d;
                    prefHeight = isControlButtonShown()? Math.max(prefHeight, snapSize(arrowRippler.prefHeight(width))) : 0;
                    prefHeight += prefHeight > 0 ? snappedTopInset() + snappedBottomInset() : 0;
                    return prefHeight;
                }
                @Override
                protected void layoutChildren() {
                    if (isControlButtonShown()) {
                        final double x = 0;
                        final double y = snappedTopInset();
                        final double width = snapSize(getWidth()) - x + snappedLeftInset();
                        final double height = snapSize(getHeight()) - y + snappedBottomInset();
                        positionArrow(arrowRippler, x, y, width, height);
                    }
                }

                private void positionArrow(final JFXRippler rippler, final double x, final double y, final double width, final double height) {
                    rippler.resize(width, height);
                    positionInArea(rippler, x, y, width, height, 0, HPos.CENTER, VPos.CENTER);
                }
            };

            arrowRippler.setPadding(new Insets(0,5,0,5));
            inner.getChildren().add(arrowRippler);
            StackPane.setMargin(arrowRippler, new Insets(0,4,0,4));
            getChildren().add(inner);

            showControlButtons = false;
            if (isControlButtonShown()) {
                showControlButtons = true;
                requestLayout();
            }
        }

        private boolean showTabsHeaderControls = false;

        private void showTabsMenu(final boolean value) {
            final boolean wasTabsMenuShowing = isControlButtonShown();
            this.showTabsHeaderControls = value;
            if (showTabsHeaderControls && !wasTabsMenuShowing) {
                arrowButton.setVisible(true);
                showControlButtons = true;
                inner.requestLayout();
                headerContainer.requestLayout();
            } else if (!showTabsHeaderControls && wasTabsMenuShowing) {
                // hide control button
                if (isControlButtonShown()) showControlButtons = true;
                else setVisible(false);
                requestLayout();
            }
        }

        private boolean isControlButtonShown() {
            return showTabsHeaderControls;
        }

        @Override
        protected double computePrefWidth(final double height) {
            double prefWidth = snapSize(inner.prefWidth(height));
            if (prefWidth > 0)  prefWidth += snappedLeftInset() + snappedRightInset();
            return prefWidth;
        }

        @Override
        protected double computePrefHeight(final double width) {
            return Math.max(getSkinnable().getTabMinHeight(), snapSize(inner.prefHeight(width))) + snappedTopInset() + snappedBottomInset();
        }

        @Override
        protected void layoutChildren() {
            final double x = snappedLeftInset();
            final double y = snappedTopInset();
            final double width = snapSize(getWidth()) - x + snappedRightInset();
            final double height = snapSize(getHeight()) - y + snappedBottomInset();
            if (showControlButtons) {
                setVisible(true);
                showControlButtons = false;
            }
            inner.resize(width, height);
            positionInArea(inner, x, y, width, height, 0, HPos.CENTER, VPos.BOTTOM);
        }
    }

}
