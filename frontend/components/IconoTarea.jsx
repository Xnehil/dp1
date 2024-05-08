import Image from "next/image";
export default function IconoTarea({ icon, description }) {

  return (
    <div>
      <div className="w-35 h-35 bg-gray-200 rounded-xl flex flex-col items-center justify-center shadow-md">
        <Image src={icon} alt="Icon" className="w-30 h-30"/>
      </div>
      <p className="mt-2 text-4xl text-gray-700 text-center">{description}</p>
    </div>
  );
}