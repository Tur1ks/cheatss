package foby.client.utils.render;

public class DraggableComponent {
    private float x, y;
    private float width, height;
    private float dragOffsetX, dragOffsetY;

    public DraggableComponent(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void startDragging(int mouseX, int mouseY) {
        dragOffsetX = mouseX - x;
        dragOffsetY = mouseY - y;
    }

    public float getDragOffsetX() {
        return dragOffsetX;
    }

    public float getDragOffsetY() {
        return dragOffsetY;
    }

    public void updateDragPosition(float mouseX, float mouseY) {
        this.x = mouseX - this.dragOffsetX;
        this.y = mouseY - this.dragOffsetY;
    }

    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public void setWidth(float width) { this.width = width; }
    public void setHeight(float height) { this.height = height; }

    public boolean isHovered(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    public void setBounds(float minX, float minY, float maxX, float maxY) {
        x = Math.max(minX, Math.min(x, maxX - width));
        y = Math.max(minY, Math.min(y, maxY - height));
    }
}
