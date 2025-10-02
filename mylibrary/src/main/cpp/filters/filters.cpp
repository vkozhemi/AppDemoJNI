//
// Created by vkozhemiakin on 28.09.25.
//

#include "filters.h"
#include <vector>
#include <algorithm>

namespace imgfilters {

    bool apply_invert(uint8_t *base, uint32_t w, uint32_t h, uint32_t stride) {
        if (!base || !w || !h || stride < w * 4) return false;
        for (uint32_t y = 0; y < h; ++y) {
            uint8_t *px = base + y * stride;
            for (uint32_t x = 0; x < w; ++x) {
                px[0] = 255 - px[0];
                px[1] = 255 - px[1];
                px[2] = 255 - px[2];
                px += 4;
            }
        }
        return true;
    }

    bool apply_invert(ImageView img) {
        return apply_invert(img.base, img.width, img.height, img.stride);
    }

    static inline void box_row(uint8_t *row, uint32_t w, int r) {
        if (r <= 0 || w == 0) return;
        std::vector<int> sb(w + 1, 0), sg(w + 1, 0), sr(w + 1, 0), sa(w + 1, 0);
        for (uint32_t i = 0; i < w; ++i) {
            const uint8_t *p = row + i * 4;
            sb[i + 1] = sb[i] + p[0];
            sg[i + 1] = sg[i] + p[1];
            sr[i + 1] = sr[i] + p[2];
            sa[i + 1] = sa[i] + p[3];
        }
        for (uint32_t x = 0; x < w; ++x) {
            const int L = std::max<int>(0, (int) x - r);
            const int R = std::min<int>((int) w - 1, (int) x + r);
            const int count = R - L + 1;
            uint8_t *p = row + x * 4;
            p[0] = (uint8_t) ((sb[R + 1] - sb[L]) / count);
            p[1] = (uint8_t) ((sg[R + 1] - sg[L]) / count);
            p[2] = (uint8_t) ((sr[R + 1] - sr[L]) / count);
            p[3] = (uint8_t) ((sa[R + 1] - sa[L]) / count);
        }
    }

    bool apply_box_blur(ImageView img, int radius) {
        if (!img.valid() || radius < 0) return false;
        if (radius == 0 || img.width == 0 || img.height == 0) return true;

        // Horizontal pass
        std::vector<uint8_t> tmp(img.stride * img.height);
        for (uint32_t y = 0; y < img.height; ++y) {
            uint8_t *dstRow = tmp.data() + y * img.stride;
            const uint8_t *srcRow = img.base + y * img.stride;
            std::copy(srcRow, srcRow + img.width * 4, dstRow);
            box_row(dstRow, img.width, radius);
        }

        // Vertical pass
        std::vector<uint8_t> col(img.height * 4);
        for (uint32_t x = 0; x < img.width; ++x) {
            for (uint32_t y = 0; y < img.height; ++y) {
                const uint8_t *p = tmp.data() + y * img.stride + x * 4;
                uint8_t *c = col.data() + y * 4;
                c[0] = p[0];
                c[1] = p[1];
                c[2] = p[2];
                c[3] = p[3];
            }
            box_row(col.data(), img.height, radius);
            for (uint32_t y = 0; y < img.height; ++y) {
                uint8_t *p = img.base + y * img.stride + x * 4;
                const uint8_t *c = col.data() + y * 4;
                p[0] = c[0];
                p[1] = c[1];
                p[2] = c[2];
                p[3] = c[3];
            }
        }
        return true;
    }

} // namespace imgfilters
