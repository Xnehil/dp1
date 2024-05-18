import Image from "next/image";
import "@/styles/Componentes.css"

export default function IconoTarea({ icon, description, styleBg = "bg-gray-200", size = "220" }) {

  return (
    <div className={`flex flex-col  `} style={{ width: `${size}px` }}>
      {/* No hay problema en combinar tailwind con estilos de frente ahí. Parace que tailwind tiene problemas con tamaños dinámicos  */}
      <div className={`${styleBg} rounded-3xl flex items-center justify-center shadow-md`} style={{ width: `${size}px`, height: `${size}px` }}>
        <Image src={icon} className="m-8" alt="Icon" width={size} height={size} />
      </div>
      <p className="mt-2 text-4xl text-gray-700 text-center font-title">{description}</p>
    </div>
  );
}