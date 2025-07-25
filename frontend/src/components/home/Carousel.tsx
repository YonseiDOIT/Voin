// src/components/Carousel.tsx

import React, { useState, useEffect } from 'react'
import useEmblaCarousel from 'embla-carousel-react'
import type { EmblaCarouselType } from 'embla-carousel';
import '@/emblaCarousel.css'

type PropType = {
    slides: React.ReactNode[]
}

const Carousel: React.FC<PropType> = ({ slides }) => {
    const [emblaRef, emblaApi] = useEmblaCarousel({
        align: 'center',
        loop: true,
    })

    const [selectedIndex, setSelectedIndex] = useState(0)
    const [scrollSnaps, setScrollSnaps] = useState<number[]>([])

    useEffect(() => {
        if (!emblaApi) return

        const onUpdate = (api: EmblaCarouselType) => {
            setScrollSnaps(api.scrollSnapList())
            setSelectedIndex(api.selectedScrollSnap())
        }

        emblaApi.on('select', onUpdate)
        emblaApi.on('reInit', onUpdate)

        onUpdate(emblaApi)

        return () => {
            emblaApi.off('select', onUpdate)
            emblaApi.off('reInit', onUpdate)
        }
    }, [emblaApi])

    return (
        <div className="embla">
            <div className="embla__viewport" ref={emblaRef}>
                <div className="embla__container">
                    {slides.map((slide, index) => (
                        <div className="embla__slide" key={index}>
                            {slide}
                        </div>
                    ))}
                </div>
            </div>

            <div className="embla__dots">
                {scrollSnaps.map((_, index) => (
                    <div
                        key={index}
                        className={'embla__dot'.concat(
                            index === selectedIndex ? ' embla__dot--selected' : ''
                        )}
                    />
                ))}
            </div>
        </div>
    )
}

export default Carousel
