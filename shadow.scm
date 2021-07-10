(define (shadow input_file output_file)
   (let* ((image (car (gimp-file-load RUN-NONINTERACTIVE input_file input_file)))
          (drawable (car (gimp-image-get-active-layer image))))
     ; (let * ((new-layer (car (gimp-layer-new
     ;                       image
     ;                       256
     ;                       256
     ;                       RGB-IMAGE
     ;                       "layer 1"
     ;                       100
     ;                       LAYER-MODE-NORMAL
     ;                      ))))
     ;     (gimp-image-add-layer image new-layer 0)
     ;     (gimp-item-transform-flip-simple new-layer ORIENTATION-VERTICAL TRUE 0)
     ;   )

     (gimp-context-set-interpolation 0)
     (gimp-desaturate drawable)
     (gimp-brightness-contrast drawable -127 127)
     (gimp-brightness-contrast drawable -127 127)
     (gimp-brightness-contrast drawable -127 127)
     (gimp-brightness-contrast drawable -127 127)
     (gimp-image-scale image 256 256)
     (gimp-image-resize image 512 512 128 0)
     (gimp-layer-resize-to-image-size drawable)
     (gimp-item-transform-flip-simple drawable ORIENTATION-VERTICAL TRUE 0)
     ; Set opacity to 100%
     (gimp-layer-set-opacity drawable 100)


     (let loopa ((y 20))
         (if (< y 300) ;300
         (begin
             (let loop ((x -200)) ;-200
                 (if (< x 100) ;100
                 (begin
                     ; Create a new layer
                     (define new-layer (car (gimp-layer-copy drawable 0)))
                     ; Add the new layer to the image
                     (gimp-image-add-layer image new-layer 0)
                     (gimp-layer-set-opacity new-layer 0.05) ;0.05
                     (gimp-layer-scale new-layer 512 (+ 200 y) TRUE)
                     (gimp-item-transform-shear new-layer ORIENTATION-HORIZONTAL x)
                     (gimp-layer-translate new-layer 0 -256)
                     (loop (+ x 10))
                 ))
             )
             (loopa (+ y 10))
         ))
     )

     (gimp-image-remove-layer image drawable )
     (gimp-displays-flush)

     ; (let * ((mask (car (gimp-layer-create-mask drawable 0))))
     ;         (gimp-layer-add-mask drawable mask)
     ;         (gimp-layer-set-edit-mask drawable 1)
     ;         (gimp-layer-set-show-mask drawable 1)
     ;         (gimp-layer-set-apply-mask drawable 1)
     ;         (gimp-drawable-edit-gradient-fill mask 0 0 FALSE 0 0 TRUE 1 0 1 512))

    (define final-image (car (gimp-image-duplicate image)))
    (define final-layer (car (gimp-image-merge-visible-layers image 1)))

      ; (plug-in-mblur RUN-NONINTERACTIVE image drawable 1 10 20 256 0)
    ; (plug-in-blur RUN-NONINTERACTIVE final-image final-layer)

     (gimp-file-save RUN-NONINTERACTIVE final-image final-layer output_file output_file)
     (gimp-image-delete image)
     (gimp-image-delete final-image)))
