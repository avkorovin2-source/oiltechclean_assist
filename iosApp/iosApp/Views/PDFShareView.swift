import SwiftUI
import UIKit

/// UIViewControllerRepresentable для UIActivityViewController — позволяет поделиться PDF-файлом
/// или сохранить его в «Файлы» на iPhone.
struct PDFShareView: UIViewControllerRepresentable {
    let fileURL: URL
    let onDismiss: () -> Void

    func makeUIViewController(context: Context) -> UIActivityViewController {
        let controller = UIActivityViewController(activityItems: [fileURL], applicationActivities: nil)
        controller.completionWithItemsHandler = { _, _, _, _ in
            context.coordinator.onDismiss()
        }
        return controller
    }

    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(onDismiss: onDismiss)
    }

    final class Coordinator {
        let onDismiss: () -> Void
        init(onDismiss: @escaping () -> Void) { self.onDismiss = onDismiss }
    }
}