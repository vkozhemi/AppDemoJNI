//
// Created by vkozhemiakin on 28.09.25.
//

#pragma once

#include <cstdint>

namespace imgfilters {
    /**
   * Lightweight view of RGBA image buffer.
   *
   * Holds raw pointer to pixel data and basic geometry information.
   * This struct does not own the memory;
   * the caller is responsible for allocating and freeing the underlying buffer.
   *
   * Expected pixel format: 32-bit RGBA.
   */
    struct ImageView {
        /**
         * Pointer to the beginning of image buffer.
         */
        uint8_t *base;

        /**
         * Image width in pixels.
         */
        uint32_t width;

        /**
         * Image height in pixels.
         */
        uint32_t height;

        /**
        * Number of bytes per row of the image.
        * Must be at least `width * 4` for RGBA8888 format.
        */
        uint32_t stride;

        /**
        * Validates that this ImageView describes usable image.
        *
        * @return true if buffer pointer and dimensions are valid,
        *         otherwise - false.
        */
        bool valid() const { return base && width && height && stride >= width * 4; }
    };

    /**
     * Inverts RGB channels of image buffer in-place.
     * Alpha channel is preserved.
     *
     * @param base Pointer to beginning of pixel buffer (RGBA8888).
     * @param width Width of image in pixels.
     * @param height Height of image in pixels.
     * @param stride Number of bytes per row (>= w * 4).
     * @return true if successful, false if parameters are invalid.
     */
    bool apply_invert(uint8_t *base, uint32_t w, uint32_t h, uint32_t stride);

    /**
     * Inverts RGB channels of an image represented by ImageView.
     * Alpha channel is preserved.
     *
     * @param img ImageView wrapper containing pixel buffer, width, height and stride.
     * @return true if successful, false if parameters are invalid.
     */
    bool apply_invert(ImageView img);

    /**
    * Applies separable box blur (horizontal + vertical passes) to image in-place.
    * Uses prefix-sum (integral image) for efficient averaging.
    *
    * @param img ImageView wrapper containing pixel buffer, width, height and stride.
     *              Expected format: RGBA8888.
    * @param radius Blur radius in pixels (>= 0).
     *              If 0, no blur is applied and function returns true.
    * @return true if successful, false if parameters are invalid.
    */
    bool apply_box_blur(ImageView img, int radius);
} // namespace imgfilters