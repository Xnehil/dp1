import Image from "next/image";
import "@/styles/Componentes.css"

export default function IconoTarea({ icon, description, styleBg = "bg-gray-200", size = "220" }) {

  //const containerClasses = ;
  return (
    <div className={`flex flex-col w-[${size}px] `} >
      <div className={` ${styleBg} rounded-3xl flex items-center justify-center shadow-md w-${size} h-${size} `}>
        <Image src={icon} className="m-8" alt="Icon" width={size} height={size} />
      </div>
      <p className="mt-2 text-4xl text-gray-700 text-center">{description}</p>
    </div>
  );
}