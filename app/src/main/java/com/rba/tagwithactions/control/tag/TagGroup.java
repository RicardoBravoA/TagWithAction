package com.rba.tagwithactions.control.tag;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rba.tagwithactions.R;
import com.rba.tagwithactions.control.tag.listener.OnTagClickListener;
import com.rba.tagwithactions.control.tag.listener.OnTagDeleteListener;
import com.rba.tagwithactions.control.tag.util.Util;
import com.rba.tagwithactions.model.TagEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ricardo Bravo on 7/07/16.
 */

public class TagGroup extends RelativeLayout {

    private List<Tag> mTags = new ArrayList<>();
    private LayoutInflater mInflater;
    private ViewTreeObserver mViewTreeObserver;
    private OnTagClickListener mClickListener;
    private OnTagDeleteListener mDeleteListener;
    private boolean mInitialized = false, deleteTag, DEFAULT_DELETE = true;
    private int mWidth, lineMargin, tagMargin, textPaddingLeft, textPaddingRight, textPaddingTop,
            texPaddingBottom, BACKGROUND_COLOR, BACKGROUND_COLOR_PRESSED, DEFAULT_TAG_TEXT_COLOR,
            colorDefault, colorPressed, colorText;
    public final float DEFAULT_LINE_MARGIN = 5, DEFAULT_TAG_MARGIN = 5,
            DEFAULT_TAG_TEXT_PADDING_LEFT = 8, DEFAULT_TAG_TEXT_PADDING_TOP = 5,
            DEFAULT_TAG_TEXT_PADDING_RIGHT = 8, DEFAULT_TAG_TEXT_PADDING_BOTTOM = 5,
            LAYOUT_WIDTH_OFFSET = 2;

    public TagGroup(Context ctx) {
        super(ctx, null);
        initialize(ctx, null, 0);
    }

    public TagGroup(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        initialize(ctx, attrs, 0);
    }

    public TagGroup(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);
        initialize(ctx, attrs, defStyle);
    }

    private void initialize(Context ctx, AttributeSet attrs, int defStyle) {
        mInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewTreeObserver = getViewTreeObserver();
        mViewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mInitialized) {
                    mInitialized = true;
                    drawTags();
                }
            }
        });

        BACKGROUND_COLOR = ContextCompat.getColor(ctx, R.color.colorTag);
        BACKGROUND_COLOR_PRESSED = ContextCompat.getColor(ctx, R.color.colorTagPressed);
        DEFAULT_TAG_TEXT_COLOR = ContextCompat.getColor(ctx, R.color.white);

        TypedArray typeArray = ctx.obtainStyledAttributes(attrs, R.styleable.TagGroup,
                defStyle, defStyle);
        lineMargin = (int) typeArray.getDimension(R.styleable.TagGroup_lineMargin,
                Util.dipToPx(this.getContext(), DEFAULT_LINE_MARGIN));
        tagMargin = (int) typeArray.getDimension(R.styleable.TagGroup_tagMargin,
                Util.dipToPx(this.getContext(), DEFAULT_TAG_MARGIN));
        textPaddingLeft = (int) typeArray.getDimension(R.styleable.TagGroup_textPaddingLeft,
                Util.dipToPx(this.getContext(), DEFAULT_TAG_TEXT_PADDING_LEFT));
        textPaddingRight = (int) typeArray.getDimension(R.styleable.TagGroup_textPaddingRight,
                Util.dipToPx(this.getContext(), DEFAULT_TAG_TEXT_PADDING_RIGHT));
        textPaddingTop = (int) typeArray.getDimension(R.styleable.TagGroup_textPaddingTop,
                Util.dipToPx(this.getContext(), DEFAULT_TAG_TEXT_PADDING_TOP));
        texPaddingBottom = (int) typeArray.getDimension(R.styleable.TagGroup_textPaddingBottom,
                Util.dipToPx(this.getContext(), DEFAULT_TAG_TEXT_PADDING_BOTTOM));
        deleteTag = typeArray.getBoolean(R.styleable.TagGroup_deleteTag, DEFAULT_DELETE);
        colorDefault = typeArray.getColor(R.styleable.TagGroup_tagBackgroundColor, BACKGROUND_COLOR);
        colorPressed = typeArray.getColor(R.styleable.TagGroup_tagBackgroundPressedColor, BACKGROUND_COLOR_PRESSED);
        colorText = typeArray.getColor(R.styleable.TagGroup_textColor, DEFAULT_TAG_TEXT_COLOR);
        typeArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        if (width <= 0)
            return;
        mWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTags();
    }

    private void drawTags() {

        if (!mInitialized) {
            return;
        }

        removeAllViews();

        float total = getPaddingLeft() + getPaddingRight();

        int listIndex = 1;
        int indexBottom = 1;
        int indexHeader = 1;
        Tag tagPre = null;
        for (Tag item : mTags) {
            final int position = listIndex - 1;
            final Tag tag = item;

            View tagLayout = mInflater.inflate(R.layout.item_taggroup, null);
            tagLayout.setId(listIndex);
            tagLayout.setBackgroundDrawable(getSelector(tag));

            TextView tagView = (TextView) tagLayout.findViewById(R.id.tagDescription);
            tagView.setText(tag.text);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tagView.getLayoutParams();
            params.setMargins(textPaddingLeft, textPaddingTop, textPaddingRight, texPaddingBottom);
            tagView.setLayoutParams(params);
            tagView.setTextColor(tag.tagTextColor);
            tagView.setTextSize(TypedValue.COMPLEX_UNIT_SP, tag.tagTextSize);
            tagLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onTagClick(tag, position);
                    }
                }
            });

            float tagWidth = tagView.getPaint().measureText(tag.text) + textPaddingLeft + textPaddingRight;

            TextView deletableView = (TextView) tagLayout.findViewById(R.id.tagItemDelete);
            if (tag.isDeletable) {
                deletableView.setVisibility(View.VISIBLE);
                deletableView.setText(tag.deleteIcon);
                int offset = Util.dipToPx(getContext(), 2f);
                deletableView.setPadding(offset, textPaddingTop, textPaddingRight + offset, texPaddingBottom);
                deletableView.setTextColor(tag.deleteIndicatorColor);
                deletableView.setTextSize(TypedValue.COMPLEX_UNIT_SP, tag.deleteIndicatorSize);
                deletableView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mDeleteListener != null) {
                            Tag targetTag = tag;
                            mDeleteListener.onTagDeleted(TagGroup.this, targetTag, position);
                        }
                    }
                });
                tagWidth += deletableView.getPaint().measureText(tag.deleteIcon) + textPaddingLeft + textPaddingRight;
            } else {
                deletableView.setVisibility(View.GONE);
            }

            LayoutParams tagParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            tagParams.bottomMargin = lineMargin;

            if (mWidth <= total + tagWidth + Util.dipToPx(this.getContext(), LAYOUT_WIDTH_OFFSET)) {
                tagParams.addRule(RelativeLayout.BELOW, indexBottom);
                total = getPaddingLeft() + getPaddingRight();
                indexBottom = listIndex;
                indexHeader = listIndex;
            } else {
                tagParams.addRule(RelativeLayout.ALIGN_TOP, indexHeader);
                if (listIndex != indexHeader) {
                    tagParams.addRule(RelativeLayout.RIGHT_OF, listIndex - 1);
                    tagParams.leftMargin = tagMargin;
                    total += tagMargin;
                    if (tagPre != null && tagPre.tagTextSize < tag.tagTextSize) {
                        indexBottom = listIndex;
                    }
                }

            }
            total += tagWidth;
            addView(tagLayout, tagParams);
            tagPre = tag;
            listIndex++;

        }

    }

    private Drawable getSelector(Tag tag) {
        if (tag.background != null) return tag.background;
        StateListDrawable states = new StateListDrawable();
        GradientDrawable gdNormal = new GradientDrawable();
        gdNormal.setColor(tag.layoutColor);
        gdNormal.setCornerRadius(tag.radius);
        if (tag.layoutBorderSize > 0) {
            gdNormal.setStroke(Util.dipToPx(getContext(), tag.layoutBorderSize), tag.layoutBorderColor);
        }
        GradientDrawable gdPress = new GradientDrawable();
        gdPress.setColor(tag.layoutColorPress);
        gdPress.setCornerRadius(tag.radius);
        states.addState(new int[]{android.R.attr.state_pressed}, gdPress);
        states.addState(new int[]{}, gdNormal);
        return states;
    }

    public void addTag(Tag tag) {

        mTags.add(tag);
        drawTags();
    }

    public void addTags(List<TagEntity> entityList) {
        if (entityList == null) return;
        mTags = new ArrayList<>();

        if (entityList.isEmpty())
            drawTags();


        for (int i = 0; i < entityList.size(); i++) {
            Tag tag = new Tag(entityList.get(i).getDescription());
            tag.radius = 30f;
            mTags.add(tag);
        }

    }

    public void addTags(String[] tags) {
        if (tags == null) return;
        for (String item : tags) {
            Tag tag = new Tag(item);
            addTag(tag);
        }
    }

    public List<Tag> getTags() {
        return mTags;
    }

    public void remove(int position) {
        if (position < mTags.size()) {
            mTags.remove(position);
            drawTags();
        }
    }

    public void removeAll() {
        removeAllViews();
    }

    public int getLineMargin() {
        return lineMargin;
    }

    public void setLineMargin(float lineMargin) {
        this.lineMargin = Util.dipToPx(getContext(), lineMargin);
    }

    public int getTagMargin() {
        return tagMargin;
    }

    public void setTagMargin(float tagMargin) {
        this.tagMargin = Util.dipToPx(getContext(), tagMargin);
    }

    public int getTextPaddingLeft() {
        return textPaddingLeft;
    }

    public void setTextPaddingLeft(float textPaddingLeft) {
        this.textPaddingLeft = Util.dipToPx(getContext(), textPaddingLeft);
    }

    public int getTextPaddingRight() {
        return textPaddingRight;
    }

    public void setTextPaddingRight(float textPaddingRight) {
        this.textPaddingRight = Util.dipToPx(getContext(), textPaddingRight);
    }

    public int getTextPaddingTop() {
        return textPaddingTop;
    }

    public void setTextPaddingTop(float textPaddingTop) {
        this.textPaddingTop = Util.dipToPx(getContext(), textPaddingTop);
    }

    public int getTexPaddingBottom() {
        return texPaddingBottom;
    }

    public void setTexPaddingBottom(float texPaddingBottom) {
        this.texPaddingBottom = Util.dipToPx(getContext(), texPaddingBottom);
    }

    public void setOnTagClickListener(OnTagClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void setOnTagDeleteListener(OnTagDeleteListener deleteListener) {
        mDeleteListener = deleteListener;
    }

    public class Tag {

        public int id, tagTextColor, layoutColor, layoutColorPress, deleteIndicatorColor,
                layoutBorderColor;
        public String text, deleteIcon;
        public boolean isDeletable;
        public float deleteIndicatorSize, radius, tagTextSize, layoutBorderSize;
        public Drawable background;
        public static final float DEFAULT_TAG_DELETE_INDICATOR_SIZE = 14f,
                DEFAULT_TAG_LAYOUT_BORDER_SIZE = 0f, DEFAULT_TAG_RADIUS = 200;
        public final int DEFAULT_TAG_DELETE_INDICATOR_COLOR = Color.parseColor("#ffffff"),
                DEFAULT_TAG_LAYOUT_BORDER_COLOR = Color.parseColor("#ffffff");
        public final String DEFAULT_TAG_DELETE_ICON = "×";

        public Tag(String text) {
            init(0, text, DEFAULT_TAG_TEXT_COLOR, colorText, colorDefault,
                    colorPressed, deleteTag,
                    DEFAULT_TAG_DELETE_INDICATOR_COLOR, DEFAULT_TAG_DELETE_INDICATOR_SIZE,
                    DEFAULT_TAG_RADIUS, DEFAULT_TAG_DELETE_ICON,
                    DEFAULT_TAG_LAYOUT_BORDER_SIZE, DEFAULT_TAG_LAYOUT_BORDER_COLOR);
        }

        private void init(int id, String text, int tagTextColor, float tagTextSize,
                          int layoutColor, int layoutColorPress, boolean isDeletable,
                          int deleteIndicatorColor,float deleteIndicatorSize, float radius,
                          String deleteIcon, float layoutBorderSize, int layoutBorderColor) {
            this.id = id;
            this.text = text;
            this.tagTextColor = tagTextColor;
            this.tagTextSize = tagTextSize;
            this.layoutColor = layoutColor;
            this.layoutColorPress = layoutColorPress;
            this.isDeletable = isDeletable;
            this.deleteIndicatorColor = deleteIndicatorColor;
            this.deleteIndicatorSize = deleteIndicatorSize;
            this.radius = radius;
            this.deleteIcon = deleteIcon;
            this.layoutBorderSize = layoutBorderSize;
            this.layoutBorderColor = layoutBorderColor;
        }
    }

}
