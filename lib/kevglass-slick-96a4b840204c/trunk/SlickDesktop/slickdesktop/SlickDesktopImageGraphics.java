/*
 * Copyright (c) 2003-2006 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package slickdesktop;

import java.awt.Graphics2D;
import org.newdawn.slick.SlickException;

/**
 * This abstract class provides methods to paint on a {@link com.jme.image.Image} via the awt {@link Graphics2D}.
 */
public abstract class SlickDesktopImageGraphics extends Graphics2D {

    /**
     * @param width of the image
     * @param height of the image
     * @param paintedMipMapCount number of mipmaps that are painted, rest is drawn by image copying, 0 for no mipmaps,
     *                           1 for a single image painted and mipmaps copied, higher values respective
     * @return a new instance of ImageGraphics matching the display system.
     */
    public static SlickDesktopImageGraphics createInstance( int width, int height/*, int paintedMipMapCount*/ ) {
        try
        {
            return new SlickDesktopAWTGraphicsCopier( width, height );
        } 
        catch (SlickException ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * where painting in {@link #update()} goes to.
     */
    protected final org.newdawn.slick.Image image;

    /**
     * Protected ctor for subclasses.
     *
     * @param image where painting in {@link #update()} goes to.
     */
    protected SlickDesktopImageGraphics( org.newdawn.slick.Image image ) {
        this.image = image;
    }

    /**
     * @return image where painting in {@link #update()} goes to
     * @see #update()
     */
    public org.newdawn.slick.Image getImage() {
        return image;
    }

    /**
     * Update a texture that contains the image from {@link #getImage()}. Only dirty areas are updated. The texture must
     * have mipmapping turned off ({@link Texture#MM_NONE}).
     *
     * @param texture texture to be updated
     * @param clean   true to mark whole area as clean after updating, false to keep dirty area for updating more textures
     */
    public abstract org.newdawn.slick.Image render( org.newdawn.slick.Graphics g, boolean clean );

    /**
     * Updates the image data.
     *
     * @see #getImage()
     */
    public abstract void renderToGraphics( org.newdawn.slick.Graphics g );

    /**
     * @return true if image/texture needs update
     */
    public abstract boolean isDirty();
}
